package com.banhui.console.rpc;


import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 通过HTTP协议调用后端服务的工具类。
 */
public interface Http
        extends AutoCloseable {
    /**
     * 执行GET操作。
     *
     * @param uri
     *         访问的资源地址。
     * @param params
     *         访问参数。
     * @return 操作结果。
     */
    CompletableFuture<Result> get(
            String uri,
            Map<String, Object> params
    );

    /**
     * 执行GET操作并返回原始字节数组。
     *
     * @param uri
     *         访问的资源地址。
     * @param params
     *         访问参数。
     * @return 操作结果。
     */
    CompletableFuture<byte[]> getRaw(
            String uri,
            Map<String, Object> params
    );

    /**
     * 执行POST操作。
     *
     * @param uri
     *         访问的资源地址。
     * @param params
     *         访问参数。
     * @return 操作结果。
     */
    CompletableFuture<Result> post(
            String uri,
            Map<String, Object> params
    );

    /**
     * 执行PUT操作。
     *
     * @param uri
     *         访问的资源地址。
     * @param params
     *         访问参数。
     * @return 操作结果。
     */
    CompletableFuture<Result> put(
            String uri,
            Map<String, Object> params
    );

    /**
     * 执行DELETE操作。
     *
     * @param uri
     *         访问的资源地址。
     * @param params
     *         访问参数。
     * @return 操作结果。
     */
    CompletableFuture<Result> delete(
            String uri,
            Map<String, Object> params
    );
}
