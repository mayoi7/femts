package com.xidian.femts.dto;

import com.xidian.femts.constants.SecurityLevel;
import lombok.Data;

/**
 * 前端抽象的文档结构，用于文档创建和更新时后端数据的接收
 *
 * @author LiuHaonan
 * @date 15:18 2020/3/5
 * @email acerola.orion@foxmail.com
 */
@Data
public class DocumentReq {
    /** 文档标题 */
    private String title;

    /** 文档内容（html形式） */
    private String content;

    /** 所在目录id */
    private Long directoryId;

    /** 文档可见级别 */
    private SecurityLevel level;
}
