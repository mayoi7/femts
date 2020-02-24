package com.xidian.femts.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 单层文档目录数据结构
 *
 * @author LiuHaonan
 * @date 11:43 2020/2/3
 * @email acerola.orion@foxmail.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirList {

    /** 目录列表 */
    private List<String> directories;

    /** 挂载在上层目录下的文档列表 */
    private List<String> manuscripts;
}
