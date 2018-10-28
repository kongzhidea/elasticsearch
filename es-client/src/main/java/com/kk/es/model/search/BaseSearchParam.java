package com.kk.es.model.search;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 搜索项 只支持几个基本类型，如int，long，string,list。
 *
 * @author zhihui.kzh
 * @create 11/9/1810:32
 */
public class BaseSearchParam implements Serializable {

    private static final long   serialVersionUID = -5830197270177560620L;

    // 分页使用，默认size见内部定义
    private Page page = new Page();

    // 排序，默认为空，需要手动指定
    private List<Sorted> sorted;


    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Sorted> getSorted() {
        return sorted;
    }

    public void setSorted(List<Sorted> sorted) {
        this.sorted = sorted;
    }
}
