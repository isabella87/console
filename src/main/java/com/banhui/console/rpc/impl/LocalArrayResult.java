package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Result;

import java.util.Arrays;

public class LocalArrayResult
        extends LocalResult
        implements Result {
    private Object[] data;

    public LocalArrayResult(
            Object... elements
    ) {
        this.data = elements == null ? new Object[0] : elements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] array() {
        return this.data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result dump() {
        System.out.println("result: " + Arrays.toString(this.data));
        return this;
    }
}
