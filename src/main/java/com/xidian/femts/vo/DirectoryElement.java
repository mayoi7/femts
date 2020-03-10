package com.xidian.femts.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来组成单层文档目录的数据结构<br/>
 * 单层文档目录由本数据结构的数组（List）表示
 *
 * @author LiuHaonan
 * @date 11:43 2020/2/3
 * @email acerola.orion@foxmail.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectoryElement {

    /** 当前目录/文档id */
    private Long id;

    /** 目录/文档的名称 */
    private String name;

    /** 表示当前节点是否是叶节点。true：文档；false：目录 */
    private boolean leaf;
}
