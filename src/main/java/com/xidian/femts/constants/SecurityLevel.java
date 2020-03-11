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

    /** 只有创建者本人可以读写 */
    PRIVATE("仅自己可见", 0),
    /** 所有人可读，但仅自己可写*/
    UNEDITABLE("不可编辑", 1),
    /** 仅上级和自己可以读写 */
    CONFIDENTIAL("仅上级可见", 2),
    /** 任何人均可读写 */
    PUBLIC("所有人可见", 3);

    private String name;

    private int code;

    /**
     * 判断当前安全级别下，文档是否可被<b>其他人（不包含自己）</b>读取
     * @param readerLevel 读取者权限级别
     * @param creatorLevel 文档创建者权限级别
     * @return true：可以；false：不可以
     */
    public boolean canRead(int readerLevel, int creatorLevel) {
        if (readerLevel > creatorLevel) {
            // 浏览人权限更高
            return this != PRIVATE;
        } else {
            return this != PRIVATE && this != CONFIDENTIAL;
        }
    }

    /**
     * 判断当前安全级别下，文档是否可被<b>其他人（不包含自己）</b>编辑
     * @param writerLevel 编辑者权限级别
     * @param creatorLevel 文档创建者
     * @return true：可编辑；false：
     */
    public boolean canWrite(int writerLevel, int creatorLevel) {
        if (writerLevel > creatorLevel) {
            return this != PRIVATE && this != UNEDITABLE;
        } else {
            return this == PUBLIC;
        }
    }
}
