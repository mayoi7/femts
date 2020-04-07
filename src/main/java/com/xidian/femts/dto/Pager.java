package com.xidian.femts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分页数据
 *
 * @author LiuHaonan
 * @date 9:51 2020/3/30
 * @email acerola.orion@foxmail.com
 */
@Data
@AllArgsConstructor
public class Pager {

    /** 当前页号（当该数据为-1时，表示该结果没有被计算，而不是没有数据） */
    private int pageNum;

    /** 数据总条数（当该数据为-1时，表示该结果没有被计算，而不是没有数据） */
    private int total;

    /** 总页数 */
    private int pageCount;

    public Pager(int pageCount) {
        this.pageCount = pageCount;

        this.pageNum = -1;
        this.total = -1;
    }
}
