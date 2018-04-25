package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Result;

public class DefaultResult
        extends AbstractResult
        implements Result {
    public DefaultResult(
            String content
    ) {
        super(content);
    }
}
