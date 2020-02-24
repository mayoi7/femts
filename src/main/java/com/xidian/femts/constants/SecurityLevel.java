package com.xidian.femts.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>文档安全级别<p/>
 * <p>
 * 有仅自己可见、仅自己可编辑，自己和上级可见，和所有人可见四种，
 * 但是这四种情况均需要遵循以下规则：<br/>
 * 1. 如果某文档对任一员工a可见，且文档权限级别不为"仅自己可编辑"，则a无条件拥有对文档a的编辑权<br/>
 * 2. 文档创建者无条件拥有对文档的编辑权
 * </p>
 *
 * @author LiuHaonan
 * @date 11:57 2020/2/2
 * @email acerola.orion@foxmail.com
 */
@Getter
@AllArgsConstructor
public enum SecurityLevel implements CodeEnum {

    PRIVATE("仅自己可见", 0),
    /** 该级别下对所有人可见，且仅自己可以编辑 */
    UNEDITABLE("不可编辑", 1),
    /** 该级别对自己也是可见的 */
    CONFIDENTIAL("仅上级可见", 2),
    PUBLIC("所有人可见", 3);

    private String name;

    private int code;
}
