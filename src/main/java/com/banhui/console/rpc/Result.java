package com.banhui.console.rpc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Rpc调用的结果。
 */
public interface Result {
    /**
     * 获得返回结果的整数形式。
     *
     * @return 返回结果的整数形式，如果响应结果无法被解析为整数则返回{@link Optional#empty()}。
     */
    OptionalInt intValue();

    /**
     * 获得返回结果的长整数形式。
     *
     * @return 返回结果的长整数形式，如果响应结果无法被解析为长整数则返回{@link Optional#empty()}。
     */
    OptionalLong longValue();

    /**
     * 获得返回结果的字符串形式。
     *
     * @return 返回结果的字符串形式。
     */
    String stringValue();

    /**
     * 获得返回结果的布尔形式。
     *
     * @return 返回结果的布尔形式，如果响应结果无法被解析为布尔值则返回{@link Optional#empty()}。
     */
    Optional<Boolean> booleanValue();

    /**
     * 获得返回结果的十进制数形式。
     *
     * @return 返回结果的十进制数形式，如果响应结果无法被解析为十进制数则返回{@link Optional#empty()}。
     */
    Optional<BigDecimal> decimalValue();

    /**
     * 获得返回结果的双精度数形式。
     *
     * @return 返回结果的双精度数形式，如果响应结果无法被解析为双精度数则返回{@link Optional#empty()}。
     */
    OptionalDouble doubleValue();

    /**
     * 获得返回结果的日期形式。
     *
     * <p>首先将返回结果解析为长整数，然后作为时间戳转化为日期。</p>
     *
     * @return 返回结果的日期形式，如果响应结果无法被解析为日期则返回{@link Optional#empty()}。
     */
    Optional<Date> dateValue();

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
