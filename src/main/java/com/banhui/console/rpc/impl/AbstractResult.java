package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.ResultUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.unQuote;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

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
     * {@inheritDoc}
     */
    @Override
    public int intValue() {
        try {
            return ResultUtils.intValue(unQuote(this.content));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long longValue() {
        try {
            return ResultUtils.longValue(unQuote(this.content));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
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
    public boolean booleanValue() {
        try {
            return ResultUtils.booleanValue(unQuote(this.content));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal decimalValue() {
        try {
            return ResultUtils.decimalValue(unQuote(this.content));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double doubleValue() {
        try {
            return ResultUtils.doubleValue(unQuote(this.content));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date dateValue() {
        try {
            return ResultUtils.dateValue(unQuote(this.content));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] array() {
        try {
            return (Object[]) OBJECT_MAPPER.readValue(this.content, ARRAY_TYPE);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
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
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
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
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result dump() {
        System.out.println("result: " + this.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.content;
    }
}
