package com.xidian.femts.vo;

import com.xidian.femts.dto.Pager;

/**
 * 可分页的结果数据
 *
 * @author LiuHaonan
 * @date 9:44 2020/3/30
 * @email acerola.orion@foxmail.com
 */
public class PageableResultVO extends ResultVO {

    private Pager page;

    public PageableResultVO(Object object, int pageCount) {
        super(object);
        this.page = new Pager(pageCount);
    }

    public PageableResultVO(Object object, int pageNum, int total, int pageCount) {
        super(object);
        this.page = new Pager(pageNum, total, pageCount);
    }
}
