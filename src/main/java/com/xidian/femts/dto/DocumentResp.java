package com.xidian.femts.dto;

import com.xidian.femts.entity.Manuscript;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用于给前端响应的文档数据类型
 * @author LiuHaonan
 * @date 10:15 2020/3/10
 * @email acerola.orion@foxmail.com
 */
@Data
@NoArgsConstructor
public class DocumentResp {

    /** 文档id */
    private Long id;

    /** 所在目录id*/
    private Long directoryId;

    /** 文档标题 */
    private String title;

    /** 文档html内容 */
    private String content;

    /** 文档创建者姓名 */
    private String creator;

    /** 文档创建时间 */
    private Date created;

    /**  文档安全级别枚举值 */
    private int level;

    /** 文档最近一编辑者姓名 */
    private String editor;

    /** 文档最近编辑时间 */
    private Date edited;

    public DocumentResp(Manuscript manuscript, String content, String creator, String editor) {
        this.id = manuscript.getId();
        this.directoryId = manuscript.getDirectoryId();
        this.title = manuscript.getTitle();
        this.level = manuscript.getLevel().getCode();
        this.created = manuscript.getCreatedAt();
        this.edited = manuscript .getModifiedAt();

        this.content = content;
        this.creator = creator;
        this.editor = editor;
    }

}
