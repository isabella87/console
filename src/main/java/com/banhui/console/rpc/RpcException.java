package com.banhui.console.rpc;

import java.net.URI;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class RpcException
        extends RuntimeException {
    private final String method;
    private final URI uri;
    private final int statusCode;

    public RpcException(
            String method,
            URI uri,
            int statusCode,
            String message
    ) {
        super(message);

        this.method = trimToEmpty(method);
        this.uri = uri;
        this.statusCode = statusCode;
    }

    public final String getMethod() {
        return this.method;
    }

    public final int getStatusCode() {
        return this.statusCode;
    }

    public final URI getUri() {
        return this.uri;
    }
}
