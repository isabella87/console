package com.banhui.console.rpc;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.xx.armory.commons.Validators.notNull;

public final class ResultUtils {
    private ResultUtils() {
    }

    public static String stringValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");

        final Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    public static String[] stringValues(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");

        final Object value = map.get(key);
        if (value == null) {
            return null;
        }

        if (value.getClass().isArray()) {
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
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection<?> list = Collection.class.cast(value);
            return list.stream().map(element -> element != null ? element.toString() : null).toArray(String[]::new);
        } else {
            return null;
        }
    }

    public static Integer intValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException ex) {
                return null;
            }
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            return null;
        }
    }

    public static Long longValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ex) {
                return null;
            }
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            return null;
        }
    }

    public static BigDecimal decimalValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException ex) {
                return null;
            }
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return new BigDecimal(value.toString());
        } else {
            return null;
        }
    }

    public static Boolean booleanValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");

        final Object value = map.get(key);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            final String text = ((String) value).trim().toLowerCase();
            if ("true".equals(text) || "yes".equals(text) || "on".equals(text) || "1".equals(text)) {
                return true;
            } else if ("false".equals(text) || "no".equals(text) || "off".equals(text) || "0".equals(text)) {
                return false;
            } else {
                return null;
            }
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        } else {
            return null;
        }
    }

    public static Date dateValue(
            Map<String, Object> map,
            String key
    ) {
        notNull(map, "map");

        final Long timestamp = longValue(map, key);
        return timestamp != null ? new Date(timestamp) : null;
    }

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
}
