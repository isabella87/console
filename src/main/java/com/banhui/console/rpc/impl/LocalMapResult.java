package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Result;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalMapResult
        extends LocalResult
        implements Result {
    private Map<String, Object> data;

    public LocalMapResult() {
        this.data = new HashMap<>();
    }

    public LocalMapResult(
            Map<? extends String, ? extends Object> data
    ) {
        this.data = new HashMap<>();

        if (data != null) {
            this.data.putAll(data);
        }
    }

    public LocalMapResult put(
            String key,
            Object value
    ) {
        this.data.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> map() {
        return Collections.unmodifiableMap(this.data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result dump() {
        System.out.println("result: " + this.data.toString());
        return this;
    }
}
