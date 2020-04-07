package com.xidian.femts.service;

import com.xidian.femts.constants.FileType;
import com.xidian.femts.core.FileSigner;
import com.xidian.femts.dto.JudgeResult;
import com.xidian.femts.entity.Content;
import com.xidian.femts.entity.Manuscript;
import com.xidian.femts.entity.Mark;
import com.xidian.femts.vo.SimpleDocInterface;

import java.io.File;
import java.util.List;

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
     * 根据文件中的标识符来查询文件id
     * @param file 文件数据对象
     * @param fileType 文件格式
     * @return 返回查询到的文档id，如果不存在则返回null
     */
    Long findIdByFile(File file, FileType fileType);

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
     * 在数据库中创建文件（禁止更新，会将id强行置空）
     * @param id 文档id，如果为空，则表示创建文档
     * @param manuscript 文档数据
     * @param hash 文件hash，如果为空表示文件不上传文件系统
     * @return 返回插入后数据库的数据
     */
    Manuscript saveOrUpdateFile(Long id, Manuscript manuscript, String hash);


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
     * @return 更新后的文档内容
     */
    Content updateContent(Long contentId, String content);

    /**
     * 根据文件hash查找数据库中存在的文档id
     * @param hash 文件hash（md5）
     * @return 如果为空，说明查不到数据；如果不为空，则返回的是数据表中的数据
     */
    Mark findIdByHash(String hash);

    /**
     * 统计文档个数
     * @return 返回数据库中现存文档总数（会包含已删除的文档，所以可能会有误差）
     */
    Long countManuscript();

    /**
     * 模糊前缀匹配标题查询
     * @param title 标题
     * @return 返回查询结果
     */
    List<SimpleDocInterface> fuzzyFindByTitle(String title);
}
