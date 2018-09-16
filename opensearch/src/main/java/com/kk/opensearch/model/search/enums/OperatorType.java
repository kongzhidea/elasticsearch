package com.kk.opensearch.model.search.enums;

public enum OperatorType {

    // filter
    equal("="),
    unequal("!="), // equal 和 unequal 适合int，LITERAL 以及对应的array类型。
    gte(">="),
    lte("<="),
    gt(">"),
    lt("<"), // 大、小于适合 int 类型


    // 原生number类型支持，ARRAY和LITERAL类型需要构造filter。
    // 要求field为List类型
    in("="), // 适用 list类型的field
    notin("!="), // 适用 list类型的field

    // query
    like("like"), // 字符串 equal 不分词
    analysis("analysis"), // 字符串 equal 分词
    ;


    private String cron;

    OperatorType(String cron) {
        this.cron = cron;
    }

    public String getCron() {
        return cron;
    }
}
