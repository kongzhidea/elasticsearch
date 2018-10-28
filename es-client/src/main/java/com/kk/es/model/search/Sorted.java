package com.kk.es.model.search;


import com.kk.es.model.search.enums.Ordered;

/**
 * @author zhihui.kzh
 * @create 11/9/1815:35
 */
public class Sorted {
    private String field;

    // 默认降序
    private Ordered ordered = Ordered.DECREASE;

    public Sorted() {
    }

    public Sorted(String field) {
        this.field = field;
    }

    public Sorted(String field, Ordered ordered) {
        this.field = field;
        this.ordered = ordered;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }
}
