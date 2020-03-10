package com.xidian.femts.controller;

import com.xidian.femts.constants.FileType;
import com.xidian.femts.constants.OptionType;
import com.xidian.femts.constants.SecurityLevel;
import com.xidian.femts.constants.UserQueryCondition;
import com.xidian.femts.core.FileSigner;
import com.xidian.femts.dto.JudgeResult;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.Mark;
import com.xidian.femts.entity.User;
import com.xidian.femts.service.*;
import com.xidian.femts.utils.File2HtmlUtils;
import com.xidian.femts.utils.TokenUtils;
import com.xidian.femts.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.xidian.femts.utils.MulFileUtils.changeMulFileToFile;
import static com.xidian.femts.utils.TokenUtils.generateSingleMark;
import static org.springframework.http.HttpStatus.*;

/**
 * @author LiuHaonan
 * @date 13:08 2020/1/21
 * @email acerola.orion@foxmail.com
 */
@RestController
@RequestMapping("/api/1.0/file")
@Slf4j
public class FileSystemController {

    @Value("${fastdfs.base.url}")
    private String FASTDFS_BASE_URL;

    private final StorageService storageService;

    private final HistoryService historyService;

    private final DirectoryService directoryService;

    private final InternalCacheService cacheService;

    private final ManuscriptService manuscriptService;

    private final UserService userService;

    public FileSystemController(StorageService storageService, ManuscriptService manuscriptService, UserService userService, InternalCacheService cacheService, HistoryService historyService, DirectoryService directoryService) {
        this.storageService = storageService;
        this.manuscriptService = manuscriptService;
        this.userService = userService;
        this.cacheService = cacheService;
        this.historyService = historyService;
        this.directoryService = directoryService;
    }

    /**
     * 文件上传流程：<br/>
     *     1. 获取文件格式<br/>
     *     2. 检查文件中是否有之前埋的hash码，如果有则说明该文件已经存在数据库中<br/>
     *          （压缩格式文件的检查需要在本地创建一份文件）<br/>
     *     3. 如果已经被上传过，则直接返回文档数据，否则进入下一步<br/>
     *     4. 将文件上传到fastdfs服务器上<br/>
     *     5. 将文件信息保存到数据库中<br/>
     *     6. 返回文件数据<br/>
     * </p>
     * @param mulFile Dropzone
     * @param directoryId 文档保存目录
     * @param level 文档安全级别（可视级别）
     * @return 文件上传是否成功的通知
     */
    @PostMapping("upload/{directoryId}")
    public ResultVO upload(MultipartFile mulFile,
                           @PathVariable("directoryId") Long directoryId,
                           @RequestParam(value = "level", defaultValue = "PUBLIC") SecurityLevel level) {
        // 1. 获取登陆用户的id
        String username = TokenUtils.getLoggedUserInfo();
        if (username == null) {
            log.error("[AUTH] current user is not logged in");
            return new ResultVO(BAD_REQUEST, "当前用户未登录");
        }
        User user = userService.findByCondition(username, UserQueryCondition.USERNAME);
        if (user == null) {
            log.error("[AUTH] logged user is not existed <username: {}>", username);
            return new ResultVO(INTERNAL_SERVER_ERROR, "登陆用户不存在");
        }
        Long userId = user.getId();

        if (mulFile == null) {
           log.warn("[FileSystem] receive file failed");
           return new ResultVO(BAD_REQUEST, "接收失败，请重新上传文件");
        }
        // 2. 将文件转换为字节数组（用于向fastdfs中存储）
        byte[] bytes;
        try {
            bytes = mulFile.getBytes();
        } catch (IOException e) {
            log.error("[FileSystem] get bytes from multipart file failed <file_name: {}>",
                    mulFile.getOriginalFilename(), e);
            return new ResultVO(BAD_REQUEST, "从文件中提取字节数据错误");
        }
        FileType fileType = FileType.getFileType(mulFile);
        if (fileType == null) {
            log.error("[FileSystem] can not get file type <name: {}>", mulFile.getOriginalFilename());
            return new ResultVO(BAD_REQUEST, "文件格式有误，请重新上传");
        }

        // 3. 获取File类型对象
        File file;
        switch (fileType) {
            case WORD2003:
            case WORD2007:
            case OFD:
                file = saveFile2Local(mulFile);break;
            case PDF:
            case TXT:
            case CUSTOM:
            default:
                file = changeMulFileToFile(mulFile);break;
        }
        if (file == null) {
            return new ResultVO(INTERNAL_SERVER_ERROR, "文件数据异常");
        }
        // 4. 根据文件中是否埋有标识符，来判断文件是否被上传过，如果没有上传则会获取标识符并埋在文件中（txt格式除外）
        JudgeResult<FileSigner.FileData> checkResult
                = manuscriptService.checkIfFileUploadedOrSetHash(file, bytes, fileType);
        FileSigner.FileData fileData = checkResult.getData();
        if (checkResult.isTrue()) {
            // 如果返回true，说明文档已经被上传，返回文档数据
            Mark mark = manuscriptService.findIdByHash(fileData.hash);
            if (mark == null) {
                log.error("[FileSystem] found sign in file but not in database <file_name: {}>",
                        mulFile.getOriginalFilename());
                return new ResultVO(BAD_REQUEST, "文件中的标识符在数据库中不存在，请检查文件是否正确");
            }
            return new ResultVO(cacheService.findById_Manuscript(mark.getManuscriptId()));
        }
        String hash = fileData.hash;
        bytes = fileData.bytes;

        // 5. 将文件上传到fastdfs服务器
        String fileId = storageService.upload(bytes, fileType.getName());
        if (fileId == null) {
            log.error("[FileSystem] file upload failed <name: {}>", mulFile.getOriginalFilename());
            return new ResultVO(INTERNAL_SERVER_ERROR, "服务器文件上传失败，请稍后重试");
        }
        // 6. 在数据库中保存文档信息
        String htmlContent = File2HtmlUtils.convertFileBytesToHTML(bytes, fileType);
        Long contentId = manuscriptService.saveContent(htmlContent);
        Manuscript manuscript = manuscriptService.saveFile(userId, directoryId, contentId, mulFile.getName(), fileType, fileId, hash, level);
        if (manuscript == null) {
            log.error("[FileSystem] save file to database failed <name: {}>", mulFile.getOriginalFilename());
            return new ResultVO(INTERNAL_SERVER_ERROR, "文件上传数据库失败");
        }
        // 7. 在目录表中记录信息
        if (directoryService.appendManuscript(directoryId, manuscript.getId()) == null) {
            // 只打印log，如果影响较大可以后期加上重试操作，但不要中止整个流程
            log.error("[DIR] directory append failed <parent_id: {}, doc_id: {}>",
                    directoryId, manuscript.getId());
        }
        // 8. 添加操作记录
        historyService.addOptionHistory(userId, manuscript.getId(), OptionType.CREATE);
        return new ResultVO(CREATED, manuscript);
    }

    /**
     * 将文件保存到本地
     * @param mulFile 浏览器上传的文件数据
     * @return 保存后的文件数据
     */
    private File saveFile2Local(MultipartFile mulFile) {
        String basePath = "file/temp/";
        // 上传文件重命名
        String newName = generateSingleMark() + "-" + mulFile.getOriginalFilename();
        try {
            FileUtils.copyInputStreamToFile(mulFile.getInputStream(), new File(basePath, newName));
        } catch (IOException ex) {
            log.error("[FileSystem] file save to local failed <name: {}>" + mulFile.getOriginalFilename(), ex);
            return null;
        }
        return new File(basePath + newName);
    }

}
