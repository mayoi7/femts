package com.xidian.femts.vo;

import com.xidian.femts.constants.Operation;
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

    /** 操作对象的名称 */
    private String name;

    /** 操作类型，false：用户；true：文档 */
    private Boolean type;

    /** 操作内容 */
    private Operation operation;

    /** 操作时间 */
    private Date time;

    /** 备注信息 */
    private String remark;

    public OperationHistory(History history, String name) {
        this.type = history.getType();
        this.operation = history.getOperation();
        this.time = history.getTime();
        this.remark = history.getRemark();

        this.name = name;
    }
}
