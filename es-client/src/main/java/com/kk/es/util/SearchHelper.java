package com.kk.es.util;

import com.kk.es.model.search.BaseSearchParam;
import com.kk.es.model.search.annotations.SearchKey;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * @author zhihui.kzh
 * @create 11/9/1817:57
 */
public class SearchHelper {
    private static final Logger logger = LoggerFactory.getLogger(SearchHelper.class);

    /**
     * query
     *
     * @param param
     * @return 如果没有则返回null，不能是空串。
     */
    public static BoolQueryBuilder buildQueryParam(BaseSearchParam param) {
        List<Field> fieldList = Reflections.getAllFields(param.getClass());
        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        for (Field f : fieldList) {
            SearchKey sk = f.getAnnotation(SearchKey.class);
            if (sk == null) {
                continue;
            }
            Object fieldValue = Reflections.getFieldValue(param, f);

            String columnName = sk.name();

            if (fieldValue == null || columnName == null) {
                continue;
            }

            switch (sk.operatorType()) {
                case term:
                    qb.must(QueryBuilders.termQuery(columnName, fieldValue));
                    break;
                case match:
                    qb.must(QueryBuilders.matchQuery(columnName, fieldValue));
                    break;
                case match_phrase:
                    qb.must(QueryBuilders.matchPhraseQuery(columnName, fieldValue));
                    break;
                default:
                    break;
            }
        }
        return qb;
    }

    /**
     * query
     *
     * @param param
     * @return 如果没有则返回null，不能是空串。
     */
    public static BoolFilterBuilder buildFilterParam(BaseSearchParam param) {
        List<Field> fieldList = Reflections.getAllFields(param.getClass());
        BoolFilterBuilder fb = FilterBuilders.boolFilter();

        for (Field f : fieldList) {
            SearchKey sk = f.getAnnotation(SearchKey.class);
            if (sk == null) {
                continue;
            }
            Object fieldValue = Reflections.getFieldValue(param, f);

            String columnName = sk.name();

            if (fieldValue == null || columnName == null) {
                continue;
            }

            switch (sk.operatorType()) {
                case equal:
                    fb.must(FilterBuilders.termFilter(columnName, fieldValue));
                    break;
                case gt:
                    fb.must(FilterBuilders.rangeFilter(columnName).gt(fieldValue));
                    break;
                case lt:
                    fb.must(FilterBuilders.rangeFilter(columnName).lt(fieldValue));
                    break;
                case gte:
                    fb.must(FilterBuilders.rangeFilter(columnName).gte(fieldValue));
                    break;
                case lte:
                    fb.must(FilterBuilders.rangeFilter(columnName).lte(fieldValue));
                    break;
                case unequal:
                    fb.mustNot(FilterBuilders.termFilter(columnName, fieldValue));
                    break;
                case in:
                    if(fieldValue instanceof Collection) {
                        fb.must(FilterBuilders.inFilter(columnName, ((Collection) fieldValue).toArray()));
                    }
                    break;
                case notin:
                    if(fieldValue instanceof Collection) {
                        fb.mustNot(FilterBuilders.inFilter(columnName, ((Collection) fieldValue).toArray()));
                    }
                    break;
                default:
                    break;
            }
        }
        return fb;
    }
}
