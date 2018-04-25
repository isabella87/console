package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Result;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.unwrap;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

/**
 * RPC调用结果的基础实现。
 */
public abstract class AbstractResult
        implements Result {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JavaType ARRAY_TYPE = TypeFactory.defaultInstance().constructArrayType(Object.class);
    private static final JavaType MAP_TYPE = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class);
    private static final JavaType LIST_TYPE = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, MAP_TYPE);

    private String content;

    protected AbstractResult(
            String content
    ) {
        this.content = trimToEmpty(content);
    }

    /**
     * 如果内容被引号包含则去掉引号。
     * <p>这里的引号包含单引号({@literal '})和双引号({@literal "})。</p>
     *
     * @param content
     *         原始内容。
     * @return 如果原始内容包含引号则返回去掉引号的内容，否则返回原始内容。
     */
    private String unQuote(
            String content
    ) {
        return unwrap(unwrap(content, '\''), '"');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalInt intValue() {
        if (this.content.isEmpty() || this.content.length() > 16) {
            return OptionalInt.empty();
        } else {
            try {
                return OptionalInt.of(Integer.parseInt(unQuote(this.content)));
            } catch (NumberFormatException ignore) {
                return OptionalInt.empty();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalLong longValue() {
        if (this.content.isEmpty() || this.content.length() > 16) {
            return OptionalLong.empty();
        } else {
            try {
                return OptionalLong.of(Long.parseLong(unQuote(this.content)));
            } catch (NumberFormatException ignore) {
                return OptionalLong.empty();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String stringValue() {
        return unQuote(this.content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Boolean> booleanValue() {
        if (this.content.isEmpty() || this.content.length() > 16) {
            return Optional.empty();
        } else {
            final String v = unQuote(this.content).toLowerCase();
            if ("true".equals(v) || "on".equals(v) || "yes".equals(v) || toLong(v) > 0) {
                return Optional.of(true);
            } else {
                return Optional.of(false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BigDecimal> decimalValue() {
        if (this.content.isEmpty() || this.content.length() > 64) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(new BigDecimal(unQuote(this.content)));
            } catch (NumberFormatException ignore) {
                return Optional.empty();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalDouble doubleValue() {
        if (this.content.isEmpty()) {
            return OptionalDouble.empty();
        } else {
            try {
                return OptionalDouble.of(Double.parseDouble(unQuote(this.content)));
            } catch (NumberFormatException ignore) {
                return OptionalDouble.empty();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Date> dateValue() {
        if (this.content.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(new Date(Long.parseLong(unQuote(this.content))));
            } catch (NumberFormatException ignore) {
                return Optional.empty();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] array() {
        try {
            return (Object[]) OBJECT_MAPPER.readValue(this.content, ARRAY_TYPE);
        } catch (IOException ignore) {
            return new Object[0];
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> list() {
        try {
            return (List<Map<String, Object>>) OBJECT_MAPPER.readValue(this.content, LIST_TYPE);
        } catch (IOException ignore) {
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> map() {
        try {
            return (Map<String, Object>) OBJECT_MAPPER.readValue(this.content, MAP_TYPE);
        } catch (IOException ignore) {
            return Collections.emptyMap();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.content;
    }
}
