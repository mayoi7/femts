package com.xidian.femts.service;

import com.xidian.femts.constants.FileType;
import com.xidian.femts.constants.SecurityLevel;
import com.xidian.femts.core.FileSigner;
import com.xidian.femts.dto.JudgeResult;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.Mark;

import java.io.File;

/**
 * 文稿服务接口（Manuscript表和Mark表）
 *
 * @author LiuHaonan
 * @date 12:21 2020/2/2
 * @email acerola.orion@foxmail.com
 */
public interface ManuscriptService {

    /**
     * 根据文档标题和作者信息进行查询
     * @param userId 作者id
     * @param title 文档标题（禁止为空）（<b>禁止为纯数字</b>）
     * @return 数据库中文稿对象，为空说明标题不存在
     */
    Manuscript findByTitle(Long userId, String title);

    /**
     * 根据文稿id查找文档标题
     * @param id 文稿id（不可为空）
     * @return 文档标题，为空说明id不正确
     */
    String findTitleById(Long id);

    /**
     * 检查文件是否被上传过，如果没有被上传，则向文件中种标识符
     * @param file 文件数据
     * @param bytes 字节数据
     * @param type 文件类型
     * @return 返回 {@link JudgeResult} 中的布尔值表示文件是否已经在之前被上传过，
     * 数据类型为 {@link FileSigner.FileData}，包含了新字节数组和文件hash值
     */
    JudgeResult<FileSigner.FileData> checkIfFileUploadedOrSetHash(File file, byte[] bytes, FileType type);

    /**
     * 在数据库中创建文件（禁止更新）
     * @param userId 用户id（创建人）
     * @param directoryId 保存的目录id
     * @param contentId 文档内容id
     * @param fileName 文件名
     * @param fileType 文件格式
     * @param fileId fastdfs中的文件id
     * @param hash 文件hash
     * @param level 文件权限级别
     * @return 返回插入后数据库的数据
     */
    Manuscript saveFile(Long userId, Long directoryId, Long contentId, String fileName, FileType fileType, String fileId,
                        String hash, SecurityLevel level);

    /**
     * 保存文档内容
     * @param content 文档html内容
     * @return 文档内容id
     */
    Long saveContent(String content);

    /**
     * 更新文档内容
     * @param contentId 文档内容id
     * @param content 更新后的文档内容
     * @return 更新后的文档内容id
     */
    Long updateContent(Long contentId, String content);

    /**
     * 根据文件hash查找数据库中存在的文档id
     * @param hash 文件hash（md5）
     * @return 如果为空，说明查不到数据；如果不为空，则返回的是数据表中的数据
     */
    Mark findIdByHash(String hash);
}
