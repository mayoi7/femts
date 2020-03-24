package com.xidian.femts.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 系统的一些统计数据
 *
 * @author LiuHaonan
 * @date 20:30 2020/3/24
 * @email acerola.orion@foxmail.com
 */
@Data
@AllArgsConstructor
public class SystemCount {

    /** 注册人数 */
    private long registered;

    /** 激活人数 */
    private long actived;

    /** 在线人数 */
    private long online;

    /** 文档总数 */
    private long document;
}
