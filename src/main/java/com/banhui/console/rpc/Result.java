package com.banhui.console.rpc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Rpc调用的结果。
 */
public interface Result {
    /**
     * 获得返回结果的整数形式。
     *
     * @return 返回结果的整数形式。
     * @throws IllegalStateException
     *         如果响应结果无法被转化为整数。
     */
    int intValue();

    /**
     * 获得返回结果的长整数形式。
     *
     * @return 返回结果的长整数形式。
     * @throws IllegalStateException
     *         如果响应结果无法被转化为长整数。
     */
    long longValue();

    /**
     * 获得返回结果的字符串形式。
     *
     * @return 返回结果的字符串形式。
     */
    String stringValue();

    /**
     * 获得返回结果的布尔值。
     *
     * @return 返回结果的布尔值。
     * @throws IllegalStateException
     *         如果响应结果无法被转化布尔值。
     */
    boolean booleanValue();

    /**
     * 获得返回结果的十进制值。
     *
     * @return 返回结果的十进制值。
     * @throws IllegalStateException
     *         如果响应结果无法被转化为十进制值。
     */
    BigDecimal decimalValue();

    /**
     * 获得返回结果的双精度值。
     *
     * @return 返回结果的双精度数值。
     * @throws IllegalStateException
     *         如果响应结果无法被转化为双精度值。
     */
    double doubleValue();

    /**
     * 获得返回结果的日期值。
     *
     * @return 返回结果的日期值。
     * @throws IllegalStateException
     *         如果响应结果无法被转化为日期值。
     */
    Date dateValue();

    /**
     * 获得返回结果的数组形式。
     *
     * @return 返回结果的数组形式，如果响应结果无法被解析为数组则返回空数组。
     */
    Object[] array();

    /**
     * 获得返回结果的映射列表形式。
     *
     * @return 返回结果的映射列表形式，如果响应结果无法被解析为列表则返回空列表。
     */
    List<Map<String, Object>> list();

    /**
     * 获得返回结果的映射形式。
     *
     * @return 返回结果的映射形式，如果响应结果无法被解析为映射则返回空映射。
     */
    Map<String, Object> map();

    /**
     * 在控制台输出返回结果的内容。
     *
     * @return 返回结果本身。
     */
    Result dump();

    /**
     * 废弃返回结果，不做任何处理。
     */
    default void none() {
    }
}
