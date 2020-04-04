package com.xidian.femts.vo;

import com.xidian.femts.entity.Manuscript;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 文档详细信息数据，用于管理员查询
 * @author LiuHaonan
 * @date 18:04 2020/3/30
 * @email acerola.orion@foxmail.com
 */
@Data
@AllArgsConstructor
public class DocumentInfo {

    /** 文档在数据库中的主键id */
    private Long id;

    /** fastdfs中的文件id */
    private String fileId;

    /** 文档标题 */
    private String title;

    /** 文档格式 */
    private String format;

    /** 文档可视级别 */
    private int level;

    /** 内容的html格式 */
    private String content;

    /** 文档创建者的用户名 */
    private String creator;

    /** 文档创建时间 */
    private Date createAt;

    /** 文档最近编辑人的用户名 */
    private String editor;

    /** 文档最近一次编辑时间 */
    private Date editedAt;

    public DocumentInfo(Manuscript manuscript, String content, String creator, String editor) {
        this.id = manuscript.getId();
        this.fileId = manuscript.getFileId();
        this.title = manuscript.getTitle();
        this.format = manuscript.getType().getName();
        this.level = manuscript.getLevel().getCode();
        this.createAt = manuscript.getCreatedAt();
        this.editedAt = manuscript.getModifiedAt();

        this.content = content;
        this.creator = creator;
        this.editor = editor;
    }
}
