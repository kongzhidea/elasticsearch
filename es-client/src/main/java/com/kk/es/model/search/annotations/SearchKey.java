package com.kk.es.model.search.annotations;

import com.kk.es.model.search.enums.OperatorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SearchKey {

    // 条件类型，默认等于
    OperatorType operatorType() default OperatorType.equal;

    // 索引
    String index();

    // 列名，默认为字段名
    String name() default "";

    // 如果是同一个索引或者column需要 equal检索，需要检索多个值，支持 or查询。如  ( id:1 or id:2)
    // 只equals,like,analysis 操作中支持or。
    boolean or() default false;

    // in/notIn 操作中默认false，表示采用自己构造的 OR结构的预计；true 表示采用原生的in操作（只适用于int和float类型）。
    boolean focusPlugin() default false;
}
