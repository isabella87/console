package com.banhui.console.rpc;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.unwrap;
import static org.xx.armory.commons.Validators.notBlank;
import static org.xx.armory.commons.Validators.notNull;
import static org.xx.armory.swing.UIUtils.toBooleanObject;

public final class ResultUtils {
    private ResultUtils() {
    }

    /**
     * 将字符串解析为整数。
     *
     * @param value
     *         待解析的字符串。
     * @return 解析结果。
     * @throws IllegalArgumentException
     *         如果参数{@code value}是{@code null}。
     * @throws NumberFormatException
     *         如果参数{@code value}不能被解析为整数。
     */
    public static int intValue(
            String value
    ) {
        notNull(value, "value");

        value = value.trim();
        if (value.isEmpty() || value.length() > 16) {
            throw new IllegalArgumentException("cannot cast " + value + " to int");
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 将字符串解析为长整数。
     *
     * @param value
     *         待解析的字符串。
     * @return 解析结果。
     * @throws IllegalArgumentException
     *         如果参数{@code value}是{@code null}。
     * @throws NumberFormatException
     *         如果参数{@code value}不能被解析为长整数。
     */
    public static long longValue(
            String value
    ) {
        notNull(value, "value");

        value = value.trim();
        if (value.isEmpty() || value.length() > 32) {
            throw new IllegalArgumentException("cannot cast " + value + " to long");
        } else {
            return Long.parseLong(value);
        }
    }

    /**
     * 将字符串解析为双精度数。
     *
     * @param value
     *         待解析的字符串。
     * @return 解析结果。
     * @throws IllegalArgumentException
     *         如果参数{@code value}是{@code null}。
     * @throws NumberFormatException
     *         如果参数{@code value}不能被解析为双精度数。
     */
    public static double doubleValue(
            String value
    ) {
        notNull(value, "value");

        value = value.trim();
        if (value.isEmpty() || value.length() > 40) {
            throw new IllegalArgumentException("cannot cast " + value + " to double");
        } else {
            return Double.parseDouble(value);
        }
    }

    /**
     * 将字符串解析为十进制数。
     *
     * @param value
     *         待解析的字符串。
     * @return 解析结果。
     * @throws IllegalArgumentException
     *         如果参数{@code value}是{@code null}。
     * @throws NumberFormatException
     *         如果参数{@code value}不能被解析为十进制数。
     */
    public static BigDecimal decimalValue(
            String value
    ) {
        notNull(value, "value");

        value = value.trim();
        if (value.isEmpty() || value.length() > 64) {
            throw new IllegalArgumentException("cannot cast " + value + " to decimal");
        } else {
            return new BigDecimal(value);
        }
    }

    /**
     * 将字符串解析为布尔值。
     *
     * @param value
     *         待解析的字符串。
     * @return 解析结果。
     * @throws IllegalArgumentException
     *         如果参数{@code value}是{@code null}。如果参数{@code value}不能被解析为布尔值。
     */
    public static boolean booleanValue(
            String value
    ) {
        notNull(value, "value");

        value = value.trim();
        if (value.isEmpty() || value.length() > 8) {
            throw new IllegalArgumentException("cannot cast " + value + " to boolean");
        } else {
            final Boolean result = toBooleanObject(value);
            if (result == null) {
                throw new IllegalArgumentException("cannot cast " + value + " to boolean");
            } else {
                return result;
            }
        }
    }

    /**
     * 将字符串解析为日期。
     *
     * @param value
     *         待解析的字符串。
     * @return 解析结果。
     * @throws IllegalArgumentException
     *         如果参数{@code value}是{@code null}。如果参数{@code value}不能被解析为日期。
     * @see Date#Date(long)
     */
    public static Date dateValue(
            String value
    ) {
        notNull(value, "value");

        value = value.trim();
        if (value.isEmpty() || value.length() > 32) {
            throw new IllegalArgumentException("cannot cast " + value + " to date");
        } else {
            try {
                final long timestamp = Long.parseLong(value);
                return new Date(timestamp);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("cannot cast " + value + " to date");
            }
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为字符串。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为字符串，如果指定的值不存在则返回空字符串。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}，如果参数{@code key}是{@code null}或者只包含空白字符。
     * @see Object#toString()
     */
    public static String stringValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return "";
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为字符串数组。
     * <ul>
     * <li>如果值是字符串，那么被转化为只包含一个元素的数组。</li>
     * <li>如果值是数组，那么该数组的每个元素被转化为字符串，然后构造新的数组。{@code null}元素被转化为空字符串。</li>
     * <li>如果值是集合，那么该集合的每个元素被转化为字符串，然后构造新的数组。{@code null}元素被转化为空字符串。</li>
     * </ul>
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为字符串数组，如果指定的值不存在则返回空数组。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}，如果参数{@code key}是{@code null}或者只包含空白字符。
     * @throws ClassCastException
     *         如果键对应的值无法被转化为字符串数组。
     * @see Object#toString()
     */
    public static String[] stringValues(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return new String[0];
        }

        if (value instanceof CharSequence) {
            return new String[]{value.toString()};
        } else if (value.getClass().isArray()) {
            if (value instanceof String[]) {
                return (String[]) value;
            } else {
                final String[] result = new String[Array.getLength(value)];
                for (int i = 0; i < Array.getLength(value); ++i) {
                    final Object element = Array.get(value, i);
                    result[i] = element != null ? element.toString() : null;
                }
                return result;
            }
        } else if (value instanceof Collection) {
            Collection<?> list = (Collection) value;
            return list.stream().map(element -> element != null ? element.toString() : "").toArray(String[]::new);
        } else {
            throw new ClassCastException("cannot cast " + value + " to String[]");
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为整数。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为整数，如果指定的值不存在则返回{@code null}。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}，如果参数{@code key}是{@code null}或者只包含空白字符。
     * @throws NumberFormatException
     *         键对应的值是字符串，但是无法被解析为整数。
     * @throws ClassCastException
     *         如果键对应的值存在，但是无法被转化为整数。
     * @see Object#toString()
     */
    public static Integer intValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return intValue((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new ClassCastException("cannot cast " + value + " to int");
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为长整数。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为长整数，如果指定的值不存在则返回{@code null}。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}，如果参数{@code key}是{@code null}或者只包含空白字符。
     * @throws NumberFormatException
     *         键对应的值是字符串，但是无法被解析为长整数。
     * @throws ClassCastException
     *         如果键对应的值存在，但是无法被转化为长整数。
     * @see Object#toString()
     */
    public static Long longValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return longValue((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            throw new ClassCastException("cannot cast " + value + " to long");
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为双精度数。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为长整数，如果指定的值不存在则返回{@code null}。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}，如果参数{@code key}是{@code null}或者只包含空白字符。
     * @throws NumberFormatException
     *         键对应的值是字符串，但是无法被解析为双精度数。
     * @throws ClassCastException
     *         如果键对应的值存在，但是无法被转化为双精度数。
     * @see Object#toString()
     */
    public static Double doubleValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return doubleValue((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new ClassCastException("cannot cast " + value + " to long");
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为十进制数。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为长整数，如果指定的值不存在则返回{@code null}。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}，如果参数{@code key}是{@code null}或者只包含空白字符。
     * @throws NumberFormatException
     *         键对应的值是字符串，但是无法被解析为十进制数。
     * @throws ClassCastException
     *         如果键对应的值存在，但是无法被转化为十进制数。
     * @see Object#toString()
     */
    public static BigDecimal decimalValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return decimalValue((String) value);
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return new BigDecimal(value.toString());
        } else {
            throw new ClassCastException("cannot cast " + value + " to decimal");
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为布尔值。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为长整数，如果指定的值不存在则返回{@code null}。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}。如果参数{@code key}是{@code null}或者只包含空白字符。如果键对应的值是字符串，但是无法被解析为布尔值。
     * @throws ClassCastException
     *         如果键对应的值存在，但是无法被转化为布尔值。
     * @see Object#toString()
     */
    public static Boolean booleanValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return booleanValue((String) value);
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        } else {
            throw new ClassCastException("cannot cast " + value + " to boolean");
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为长整数，然后作为时间戳转化为日期。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为日期，如果指定的值不存在则返回{@code null}。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}。如果参数{@code key}是{@code null}或者只包含空白字符。如果键对应的值是字符串，但是无法被解析为长整数。
     * @throws ClassCastException
     *         如果键对应的值存在，但是无法被转化为日期。
     * @see Object#toString()
     */
    public static Date dateValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return dateValue((String) value);
        } else if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        } else if (value instanceof Date) {
            return (Date) value;
        } else {
            throw new ClassCastException("cannot cast " + value + " to date");
        }
    }

    /**
     * 将若干参数转化为字符串数组。
     * <p>每个参数通过调用 {@link Object#toString()}方法转化为字符串，如果某个参数是{@code null}则转化为空字符串。</p>
     * <p>如果没有传入任何参数则返回空字符串数组。</p>
     *
     * @param array
     *         参数。
     * @return 字符串数组。
     * @see Object#toString()
     */
    public static String[] stringValues(
            Object... array
    ) {
        if (array == null) {
            return new String[0];
        } else {
            final String[] result = new String[array.length];

            for (int i = 0; i < array.length; ++i) {
                final Object element = array[i];
                result[i] = element != null ? element.toString() : "";
            }

            return result;
        }
    }

    /**
     * 从Map中获取指定键的值，并转化为对象列表。
     *
     * @param map
     *         原始Map。
     * @param key
     *         键，自动去掉首尾空格。
     * @return 键对应的值，并转化为对象列表。
     * @throws IllegalArgumentException
     *         如果参数{@code map}是{@code null}，如果参数{@code key}是{@code null}或者只包含空白字符。
     * @throws ClassCastException
     *         如果键对应的值无法被转化为列表。如果键对应的值能够转化为列表，但是列表的项不是Map。
     */
    public static List<Map<String, Object>> listValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");
        key = notBlank(key, "key").trim();

        final Object value = map.get(key);
        if (value == null) {
            return Collections.emptyList();
        } else if (value instanceof Collection) {
            final List<Map<String, Object>> result = new ArrayList<>(((Collection) value).size());
            final Collection<?> c = (Collection) value;
            for (final Object item : c) {
                if (item instanceof Map) {
                    result.add(((Map<?, ?>) item)
                                       .entrySet().stream()
                                       .filter(entry -> entry.getKey() != null && !Objects.equals(entry.getKey(), ""))
                                       .filter(entry -> entry.getValue() != null)
                                       .collect(toMap(
                                               entry -> entry.getKey().toString(),
                                               Map.Entry::getValue
                                       )));
                } else {
                    throw new ClassCastException("cannot cast " + item + " to Map");
                }
            }
            return result;
        } else {
            throw new ClassCastException("cannot cast " + value + " to Collection");
        }
    }

    /**
     * 将若干异步请求合并，形成新的请求。
     * <p>当所有被合并请求完成时，返回的请求也完成，任何一个被合并请求失败时，返回的请求也失败。</p>
     * <p><strong>当某个被合并的请求失败时，其它被合并的请求可能会继续执行。</strong></p>
     *
     * @param stages
     *         被合并的请求。
     * @return 新的请求。
     */
    @SafeVarargs
    public static CompletableFuture<Result[]> allDone(
            CompletableFuture<Result>... stages
    ) {
        if (stages == null) {
            return CompletableFuture.completedFuture(new Result[0]);
        }

        CompletableFuture<Result[]> result = CompletableFuture.completedFuture(new Result[stages.length]);

        for (int i = 0; i < stages.length; ++i) {
            CompletableFuture<Result> stage = stages[i];
            if (stage == null) {
                continue;
            }

            int index = i;

            result = result.thenCombine(
                    stage,
                    (l, r) -> {
                        l[index] = r;
                        return l;
                    });
        }

        return result;
    }

    /**
     * 从字节数组中加载图片。
     *
     * @param data
     *         表示图片内容的字节数组。
     * @return 已加载的图片，如果参数{@code data}是{@code null}则返回{@code null}。
     * @throws UncheckedIOException
     *         如果加载图片内容失败。
     * @see ImageIO#read(ImageInputStream)
     */
    public static Image readImage(
            byte[] data
    ) {
        if (data == null) {
            return null;
        }

        final ImageInputStream stream = new MemoryCacheImageInputStream(new ByteArrayInputStream(data));
        try {
            return ImageIO.read(stream);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * 如果内容被引号包含则去掉引号。
     * <p>这里的引号包含单引号({@literal '})和双引号({@literal "})。</p>
     *
     * @param content
     *         原始内容。
     * @return 如果原始内容包含引号则返回去掉引号的内容，否则返回原始内容。
     */
    public static String unQuote(
            String content
    ) {
        return unwrap(unwrap(content, '\''), '"');
    }
}
