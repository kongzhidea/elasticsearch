package com.kk.es.util;

import com.kk.es.model.search.BaseSearchParam;
import com.kk.es.model.search.annotations.SearchKey;
import com.kk.es.model.search.enums.OperatorType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author zhihui.kzh
 * @create 11/9/1817:57
 */
public class SearchHelper {
    private static final Logger logger = LoggerFactory.getLogger(SearchHelper.class);

//    /**
//     * query
//     *
//     * @param param
//     * @return 如果没有则返回null，不能是空串。
//     */
//    public static String buildQueryParam(BaseSearchParam param) {
//        List<Field> fieldList = Reflections.getAllFields(param.getClass());
//
//        StringBuilder queryParam = new StringBuilder();
//
//        Map<String, List<String>> orParam = new HashMap<>();
//
//        for (Field f : fieldList) {
//            SearchKey sk = f.getAnnotation(SearchKey.class);
//            if (sk == null) {
//                continue;
//            }
//            Object fieldValue = Reflections.getFieldValue(param, f);
//
//            String indexName = sk.index();
//
//            switch (sk.operatorType()) {
//                case like:
//                case analysis:
//                    if (fieldValue != null) {
//                        String split = "\"";
//                        if (sk.operatorType() == OperatorType.analysis) {
//                            split = "'";
//                        }
//
//                        if (!sk.or()) {
//                            if (queryParam.length() > 0) {
//                                queryParam.append(" AND ");
//                            }
//                            queryParam.append(indexName).append(":");
//                            queryParam.append(split).append(fieldValue).append(split);
//                        } else {
//                            List<String> orList = orParam.get(indexName);
//                            if (orList == null) {
//                                orList = new ArrayList<>();
//                                orParam.put(indexName, orList);
//                            }
//                            StringBuilder orBuilder = new StringBuilder();
//                            orBuilder.append(indexName).append(":");
//                            orBuilder.append(split).append(fieldValue).append(split);
//                            orList.add(orBuilder.toString());
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        if (orParam.size() > 0) {
//            for (String orKey : orParam.keySet()) {
//                if (queryParam.length() > 0) {
//                    queryParam.append(" AND ");
//                }
//                List<String> orList = orParam.get(orKey);
//                queryParam.append(" ( ");
//                queryParam.append(StringUtils.join(orList, " OR "));
//                queryParam.append(" ) ");
//            }
//        }
//        return queryParam.length() == 0 ? null : queryParam.toString();
//
//    }
//
//    /**
//     * filter
//     *
//     * @param param
//     * @return 如果没有则返回null，不能是空串。
//     */
//    public static String buildFilterParam(BaseSearchParam param) {
//        List<Field> fieldList = Reflections.getAllFields(param.getClass());
//
//        StringBuilder filterParam = new StringBuilder();
//        Map<String, List<String>> orParam = new HashMap<>();
//
//        for (Field f : fieldList) {
//            SearchKey sk = f.getAnnotation(SearchKey.class);
//            if (sk == null) {
//                continue;
//            }
//            String columnName = sk.name();
//            if (columnName == null || "".equals(columnName)) {
//                columnName = f.getName();
//            }
//            Object fieldValue = Reflections.getFieldValue(param, f);
//            Class fieldType = f.getType();
//
//            String split = "\"";
//            if (isNumber(fieldType)) {
//                split = "";
//            }
//
//            switch (sk.operatorType()) {
//                case equal:
//                    if (fieldValue != null) {
//                        if (!sk.or()) {
//                            if (filterParam.length() > 0) {
//                                filterParam.append(" AND ");
//                            }
//                            filterParam.append(columnName).append("=");
//                            filterParam.append(split).append(fieldValue).append(split);
//                        } else {
//                            List<String> orList = orParam.get(columnName);
//                            if (orList == null) {
//                                orList = new ArrayList<>();
//                                orParam.put(columnName, orList);
//                            }
//                            StringBuilder orBuilder = new StringBuilder();
//                            orBuilder.append(columnName).append("=");
//                            orBuilder.append(split).append(fieldValue).append(split);
//                            orList.add(orBuilder.toString());
//                        }
//                    }
//                    break;
//                case gt:
//                case lt:
//                case gte:
//                case lte:
//                case unequal:
//                    if (fieldValue != null) {
//                        if (filterParam.length() > 0) {
//                            filterParam.append(" AND ");
//                        }
//                        filterParam.append(columnName).append(sk.operatorType().getCron());
//                        filterParam.append(split).append(fieldValue).append(split);
//                    }
//                    break;
//                case in:
//                case notin:
//                    if (fieldValue != null) {
//                        List<?> valueList = (List<?>) fieldValue;
//                        Class valueType = getComponentTypeByFirstItem(valueList);
//                        if (valueType != null) {
//                            if (filterParam.length() > 0) {
//                                filterParam.append(" AND ");
//                            }
//                            // 开放搜索自带in查询，支持number类型，如: in(field, “number1|number2”)
//                            if (isNumber(valueType) && sk.focusPlugin()) {
//                                filterParam.append(sk.operatorType().name()).append("(");
//                                filterParam.append(columnName).append(",\"");
//                                filterParam.append(StringUtils.join(valueList, "|"));
//                                filterParam.append("\")");
//                            } else { // LITERAL类型
//                                filterParam.append(" ( ");
//
//                                String inSplit = "\"";
//                                if (isNumber(valueType)) {
//                                    inSplit = "";
//                                }
//
//                                for (int c = 0; c < valueList.size(); c++) {
//                                    Object value = valueList.get(c);
//                                    if(c > 0){
//                                        filterParam.append(" OR ");
//                                    }
//                                    filterParam.append(" ").append(columnName).append(" = ");
//                                    filterParam.append(inSplit).append(value).append(inSplit);
//                                }
//                                filterParam.append(" ) ");
//                            }
//                        }
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        if (orParam.size() > 0) {
//            for (String orKey : orParam.keySet()) {
//                if (filterParam.length() > 0) {
//                    filterParam.append(" AND ");
//                }
//                List<String> orList = orParam.get(orKey);
//                filterParam.append(" ( ");
//                filterParam.append(StringUtils.join(orList, " OR "));
//                filterParam.append(" ) ");
//            }
//        }
//        return filterParam.length() == 0 ? null : filterParam.toString();
//    }
//
//
//    private static boolean isNumber(Class c) {
//        if (c == int.class || c == Integer.class
//                || c == long.class || c == Long.class
//                || c == float.class || c == Float.class
//                || c == double.class || c == Double.class
//                || c == short.class || c == Short.class
//                || c == byte.class || c == Byte.class
//                ) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取数组、Collection的内部类型<br>
//     * 若非Array，或是无法识别，则返回null
//     *
//     * @param container
//     * @return
//     */
//    private final static Class<?> getComponentTypeByFirstItem(Collection<?> container) {
//        if (container != null && container.size() > 0)
//            for (Object innerItem : (Collection<?>) container)
//                if (innerItem != null) {
//                    return innerItem.getClass();
//                }
//        // 如果数组是空白，或者数组内只有null的元素，那真的是没法识别内部类型
//        return null;
//    }

}
