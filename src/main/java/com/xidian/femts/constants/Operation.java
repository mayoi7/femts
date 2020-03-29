package com.xidian.femts.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型
 *
 * @author LiuHaonan
 * @date 20:30 2020/1/16
 * @email acerola.orion@foxmail.com
 */
@Getter
@AllArgsConstructor
public enum Operation implements CodeEnum {
    CREATE("创建文档", 0),
    UPDATE("更新文档", 1),
    DELETE("删除文档", 2),
    DOWNLOAD("下载文档", 3),
    UPLOAD("上传文档", 4),
    MODIFY_USER("修改用户信息", 5);

    private String name;

    private int code;
}
