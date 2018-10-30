package com.kk.es.model.search.enums;

public enum OperatorType {

    // filter
    equal,
    unequal,
    gte,
    lte,
    gt,
    lt,
    in,
    notin,

    // query
    term, // term是代表完全匹配，也就是精确查询，搜索前不会再对搜索词进行分词，所以我们的搜索词必须是文档分词集合中的一个。
    match, // match 会分词，并计算相关性，全文检索，可以包含一部分词
    match_phrase, // match_phrase  会分词，但是需要完全匹配所有词
    ;

}
