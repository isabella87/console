package com.banhui.console.rpc;

import com.banhui.console.rpc.impl.DefaultHttp;

import java.net.URI;

import static org.xx.armory.commons.Validators.notNull;

public final class HttpManager {
    private static final Object HTTP_LOCK = new Object();
    public static URI baseUri;
    private static volatile Http http;

    private HttpManager() {
    }

    /**
     * 设置基础URI。
     *
     * @param uri
     *         基础URI。
     * @throws IllegalArgumentException
     *         如果参数{@code uri}是{@code null}，或者不是绝对路径。
     */
    public static void setBaseUri(
            URI uri
    ) {
        notNull(uri, "uri");

        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("base uri cannot be relative: " + uri);
        }

        baseUri = uri;
    }

    /**
     * 获取用于访问后端服务的HTTP工具类。
     *
     * <p>此处使用单例模式，确保所有的调用方使用的HTTP工具类都是同一个。</p>
     *
     * @return 用于访问后端服务的HTTP工具类。
     */
    public static Http getHttp() {
        Http http_ = http;
        if (http_ == null) {
            synchronized (HTTP_LOCK) {
                http_ = http;
                if (http_ == null) {
                    http = http_ = createHttp();
                }
            }
        }
        return http_;
    }

    private static Http createHttp() {
        final DefaultHttp result = new DefaultHttp();
        result.setBaseUri(baseUri);
        // TODO: 设置缓存的本地保存位置。
        return result;
    }
}
