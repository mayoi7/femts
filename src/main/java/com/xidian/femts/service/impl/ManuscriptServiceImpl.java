package com.xidian.femts.service.impl;

import com.xidian.femts.constants.FileType;
import com.xidian.femts.constants.SecurityLevel;
import com.xidian.femts.core.FileSigner;
import com.xidian.femts.dto.JudgeResult;
import com.xidian.femts.entity.Content;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.Mark;
import com.xidian.femts.repository.ContentRepository;
import com.xidian.femts.repository.ManuscriptRepository;
import com.xidian.femts.repository.MarkRepository;
import com.xidian.femts.service.InternalCacheService;
import com.xidian.femts.service.ManuscriptService;
import com.xidian.femts.utils.MulFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static com.xidian.femts.constants.HashAlgorithm.MD5;
import static com.xidian.femts.utils.HashUtils.hashBytes;

/**
 * @author LiuHaonan
 * @date 15:53 2020/2/2
 * @email acerola.orion@foxmail.com
 */
@Service
@Slf4j
public class ManuscriptServiceImpl implements ManuscriptService {

    private final ManuscriptRepository manuscriptRepository;

    private final ContentRepository contentRepository;

    private final MarkRepository markRepository;

    private final InternalCacheService cacheService;

    private final FileSigner fileSigner;
    
    public ManuscriptServiceImpl(ManuscriptRepository manuscriptRepository, FileSigner fileSigner, MarkRepository markRepository, ContentRepository contentRepository, InternalCacheService cacheService) {
        this.manuscriptRepository = manuscriptRepository;
        this.fileSigner = fileSigner;
        this.markRepository = markRepository;
        this.contentRepository = contentRepository;
        this.cacheService = cacheService;
    }

    @Override
    @Cacheable(cacheNames = "doc", key = "#userId + '$' + #title")
    public Manuscript findByTitle(Long userId, String title) {
        return manuscriptRepository.findByTitleAndAuthor(title, userId);
    }

    @Override
    @Cacheable(cacheNames = "docTitle", key = "#id")
    public String findTitleById(Long id) {
        return manuscriptRepository.findTitleById(id);
    }

    @Override
    public JudgeResult<FileSigner.FileData> checkIfFileUploadedOrSetHash(File file, byte[] bytes, FileType type) {
        // 该方法只会在文件上传时调用，数据不需要放到缓存里
        String hash;
        FileSigner.FileData fileData;
        switch (type) {
            // 压缩格式文件处理
            case WORD2003:
            case WORD2007:
            case OFD:
                hash = fileSigner.extractHashCodeFromZipFile(file.getPath());
                if (hash != null) {
                    return new JudgeResult<>(true, new FileSigner.FileData(bytes, hash));
                } else {
                    hash = fileSigner.signZipFile(file.getPath(), bytes);
                    // 返回新字节数组
                    bytes = MulFileUtils.changeFileToBytes(file);
                    return new JudgeResult<>(false, new FileSigner.FileData(bytes, hash));
                }

            // 单一格式文件处理
            case PDF:
                hash = fileSigner.extractHashCodeFromSingleFile(file);
                if (hash != null) {
                    // 如果hash不为空，直接返回原数据
                    return new JudgeResult<>(true, new FileSigner.FileData(bytes, hash));
                } else {
                    // 否则返回hash和新的字节文件
                    return new JudgeResult<>(false, fileSigner.signSingleFile(bytes));
                }

            // 不进行处理的格式文件
            case TXT:
            case CUSTOM:
            default:
                // 其他情况根据mark表的数据来判断
                hash = hashBytes(MD5, bytes);
                Mark mark = markRepository.findByHash(hash);
                // 如果mark不为空，说明该文件已经在之前被上传到数据库中
                return new JudgeResult<>(mark != null, new FileSigner.FileData(bytes, hash));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(cacheNames = "doc", key = "#userId")
    public Manuscript saveFile(Long userId, Long directoryId, Long contentId, String fileName, FileType fileType, String fileId,
                               String hash, SecurityLevel level) {
        // 保存时不知道文件id（数据表主键），缓存又是根据id来查询，所以无法添加缓存
        Manuscript manuscript = Manuscript.builder()
                .directoryId(directoryId).contentId(contentId).fileId(fileId)
                .title(fileName).type(fileType).level(level)
                .createdBy(userId).modifiedBy(userId)
                .build();

        Manuscript record = manuscriptRepository.save(manuscript);
        if (hash != null) {
            // 如果文件hash为空，说明文件未上传至文件系统，不需要存储文件标记
            Mark mark = new Mark(record.getId(), fileType, hash);
            markRepository.save(mark);
        }
        return manuscript;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveContent(String content) {
        Content record = new Content();
        record.setContent(content);
        Content saved = contentRepository.save(record);
        return saved.getId();
    }

    @Override
    @CachePut(cacheNames = "content", key = "#contentId")
    public Long updateContent(Long contentId, String content) {
        Content record = new Content(contentId, content);
        // 返回值必不为空
        return contentRepository.save(record).getId();
    }

    @Cacheable(cacheNames = "mark", key = "#hash")
    @Override
    public Mark findIdByHash(String hash) {
        return markRepository.findByHash(hash);
    }
}
