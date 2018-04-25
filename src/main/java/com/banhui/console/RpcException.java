package com.banhui.console;

import java.net.URI;

public class RpcException
        extends RuntimeException {
    private final URI uri;
    private final int statusCode;

    public RpcException(
            URI uri,
            int statusCode,
            String message
    ) {
        super(message);

        this.uri = uri;
        this.statusCode = statusCode;
    }

    public final int getStatusCode() {
        return this.statusCode;
    }

    public final URI getUri() {
        return this.uri;
    }
}
