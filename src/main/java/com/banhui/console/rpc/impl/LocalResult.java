package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Result;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public abstract class LocalResult
        implements Result {
    public LocalResult() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalInt intValue() {
        return OptionalInt.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalLong longValue() {
        return OptionalLong.empty();
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
    public Optional<Boolean> booleanValue() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BigDecimal> decimalValue() {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalDouble doubleValue() {
        return OptionalDouble.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Date> dateValue() {
        return Optional.empty();
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
