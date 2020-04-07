package com.xidian.femts.vo;

import com.xidian.femts.entity.History;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author LiuHaonan
 * @date 9:44 2020/3/29
 * @email acerola.orion@foxmail.com
 */
@Data
public class OperationHistory implements Serializable {

    /** 操作记录数据id */
    private Long id;

    /** 操作人的名称（当查询的是用户操作记录时，该属性可以为空） */
    private String operator;

    /** 被操作对象的名称（当查询的是文档操作记录时，该属性可以为空） */
    private String name;

    /** 被操作对象的id，偶尔有场景使用，作为保留字段 */
    private Long operatedId;

    /** 操作类型，false：用户；true：文档。（当查询的是文档操作记录时，该属性一律视为true）*/
    private Boolean type;

    /** 操作内容（枚举值） */
    private int operation;

    /** 操作时间 */
    private Date time;

    /** 备注信息 */
    private String remark;

    /**
     *
     * @param history 历史记录数据
     * @param name 当查询文档操作记录时，该字段代表操作人的用户名；
     *             当查询用户操作记录时，该字段代表操作对象的名称（操作对象可以是用户或文档）
     * @param isDocHistory true：表示查询的是文档操作记录；false：表示查询的是用户操作记录
     */
    public OperationHistory(History history, String name, boolean isDocHistory) {
        this.id = history.getId();
        this.type = history.getType();
        this.operation = history.getOperation().getCode();
        this.operatedId = history.getObjectId();
        this.time = history.getTime();
        this.remark = history.getRemark();

        if (isDocHistory) {
            // 如果是查询文档操作记录，则只需记录操作人的名称，文档名称用户已知
            this.operator = name;
        } else {
            // 如果是查询用户操作记录，则只需记录被操作对象的名称，操作人名称用户已知
            this.name = name;
        }
    }
}
