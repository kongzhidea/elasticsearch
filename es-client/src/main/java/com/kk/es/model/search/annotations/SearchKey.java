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

    // 列名，默认为字段名
    String name() default "";
}
