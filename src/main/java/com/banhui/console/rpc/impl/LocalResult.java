package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Result;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class LocalResult
        implements Result {
    public LocalResult() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int intValue() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long longValue() {
        return 0L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String stringValue() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean booleanValue() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal decimalValue() {
        return BigDecimal.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double doubleValue() {
        return 0D;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date dateValue() {
        return new Date(0L);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] array() {
        return new Object[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> list() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> map() {
        return Collections.emptyMap();
    }
}
