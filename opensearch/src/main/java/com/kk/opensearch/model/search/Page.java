package com.kk.opensearch.model.search;

/**
 * @author zhihui.kzh
 * @create 11/9/1815:32
 */
public class Page {

    private int pageNum = 1;

    private int pageSize = 20;

    public Page() {
    }

    public Page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public int getStart() {
        return (pageNum - 1) * pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}