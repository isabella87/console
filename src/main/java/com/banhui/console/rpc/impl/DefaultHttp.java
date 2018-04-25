package com.banhui.console.rpc.impl;

import com.banhui.console.RpcException;
import com.banhui.console.rpc.History;
import com.banhui.console.rpc.HistoryLevel;
import com.banhui.console.rpc.HistoryManager;
import com.banhui.console.rpc.Http;
import com.banhui.console.rpc.Result;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.commons.SysUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.xx.armory.commons.SysUtils.expandPath;
import static org.xx.armory.commons.SysUtils.forceMkDirs;
import static org.xx.armory.commons.SysUtils.readString;
import static org.xx.armory.commons.Validators.notNull;
import static org.xx.armory.http.HttpUtils.parseContentType;
import static org.xx.armory.http.HttpUtils.parseHttpDate;
import static org.xx.armory.http.SSLContextUtils.ignoreCert;

/**
 * 默认的HTTP工具类，使用{@link org.apache.http.client}包来访问后端。
 * <p>{@link #get(String, Map)}, {@link #post(String, Map)}, {@link #put(String, Map)}和{@link #delete(String, Map)}方法都遵循下面的异步调用规则：</p>
 * <ul>
 * <li>公共线程池中准备HTTP请求。</li>
 * <li>执行线程池中执行HTTP请求。实际是单线程。</li>
 * <li>公共线程池中解析HTTP响应。</li>
 * </ul>
 */
public final class DefaultHttp
        extends AbstractHttp
        implements Http {
    private static final RequestConfig GLOBAL_REQUEST_CONFIG = createGlobalRequestConfig();
    private static final CookieStore GLOBAL_COOKIE_STORE = createGlobalCookieStore();
    private static final CacheConfig GLOBAL_CACHE_CONFIG = createGlobalCacheConfig();
    private final Logger logger = LoggerFactory.getLogger(DefaultHttp.class);
    /**
     * HTTP请求的线程执行器。
     */
    private final ExecutorService executor;
    /**
     * 用于执行HTTP请求的客户端。
     */
    private final HttpClient httpClient;

    public DefaultHttp() {
        // 所有的请求使用单一线程处理。
        this.executor = Executors.newSingleThreadExecutor();

        File cacheDir = getCacheDir();
        if (cacheDir == null) {
            cacheDir = new File(expandPath("~/.banhuicon"));
        }
        if (!cacheDir.exists()) {
            forceMkDirs(cacheDir);
        }
        this.httpClient = CachingHttpClientBuilder.create()
                                                  .setCacheConfig(GLOBAL_CACHE_CONFIG)
                                                  .setCacheDir(cacheDir)
                                                  .setUserAgent(getClass().getSimpleName())
                                                  .setRetryHandler(StandardHttpRequestRetryHandler.INSTANCE)
                                                  .setDefaultRequestConfig(GLOBAL_REQUEST_CONFIG)
                                                  .setDefaultCookieStore(GLOBAL_COOKIE_STORE)
                                                  .setSSLContext(ignoreCert())
                                                  .setRedirectStrategy(DefaultRedirectStrategy.INSTANCE)
                                                  .build();
    }

    private static RequestConfig createGlobalRequestConfig() {
        return RequestConfig.custom()
                            .setExpectContinueEnabled(true)
                            .setContentCompressionEnabled(true)
                            .setConnectTimeout(5 * 1000)
                            .setConnectionRequestTimeout(2 * 1000)
                            .setSocketTimeout(8 * 1000)
                            .build();
    }

    private static CookieStore createGlobalCookieStore() {
        return new BasicCookieStore();
    }

    private static CacheConfig createGlobalCacheConfig() {
        return CacheConfig.custom()
                          .setSharedCache(true)
                          .setMaxCacheEntries(1024)
                          .setMaxObjectSize(128 * 1024)
                          .build();
    }

    /**
     * 预处理HTTP请求，加入一些公共的头部。
     *
     * @param request
     *         准备预处理的HTTP请求。
     * @param <T>
     *         HTTP请求的类型。
     * @return 参数{@code request}本身。
     * @throws IllegalArgumentException
     *         如果参数{@code request}是{@code null}。
     */
    protected <T extends HttpUriRequest> T prepareRequest(
            T request
    ) {
        notNull(request, "request");

        request.setHeader("Accept-Charset", "utf-8");
        request.setHeader("Accept-Encoding", "gzip,deflate");
        request.setHeader("Expect", "100-continue");

        return request;
    }

    /**
     * 获取文本内容的摘要。
     * <p>截取前{@literal 128}个字符的内容作为摘要。</p>
     *
     * @param content
     *         原文本内容。
     * @return 摘要。
     * @throws IllegalArgumentException
     *         如果参数{@code content}是{@code null}。
     */
    private String digest(
            String content
    ) {
        notNull(content, "content");

        final int MAX_DIGEST_SIZE = 128;
        String result = left(content, MAX_DIGEST_SIZE);
        if (content.length() > MAX_DIGEST_SIZE) {
            result = result + "...";
        }
        return result;
    }

    /**
     * 发送HTTP请求并获取响应。
     *
     * @param request
     *         待发送的请求。
     * @return 已获取的响应内容。
     * @throws IllegalArgumentException
     *         如果参数{@code request}是{@code null}。
     * @throws IllegalStateException
     *         如果无法处理响应的状态码。
     * @throws RpcException
     *         如果不能读取有效的响应内容。
     */
    protected final String execute(
            HttpUriRequest request
    ) {
        notNull(request, "request");

        final StringBuilder loggerBuffer = new StringBuilder();
        try {
            loggerBuffer.append(SysUtils.format("{} {} ...", request.getMethod(), request.getURI()));
            final HttpResponse response = this.httpClient.execute(request);

            // 删除末尾的省略号。
            loggerBuffer.delete(loggerBuffer.length() - 3, loggerBuffer.length());
            // 然后加入响应状态。
            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            final String reasonPhrase = trimToEmpty(statusLine.getReasonPhrase());
            loggerBuffer.append(SysUtils.format("{} {}", statusCode, reasonPhrase));

            // 解析Response的头部。
            final Date date = getDateHeader(response.getFirstHeader("Date"));
            final Long contentLength = getLongHeader(response.getFirstHeader("Content-Length"));
            final String contentType = getStringHeader(response.getFirstHeader("Content-Type"));
            final String[] contentTypeParts = parseContentType(contentType);
            final String mimeType = contentTypeParts[0];
            final String charsetName = contentTypeParts[1];
            final Charset charset = !charsetName.isEmpty() ? Charset.forName(charsetName) : null;

            final String content = readString(response.getEntity().getContent(), charset != null ? charset : UTF_8);

            // 记录History
            final HistoryLevel level = statusCode >= 200 && statusCode < 300 ? HistoryLevel.OK : HistoryLevel.ERROR;
            final String contentDigest = digest(content);

            final History history = new History(date, level, request.getMethod(), request.getURI(), statusCode, contentLength, mimeType, charset, contentDigest);
            HistoryManager.addHistory(history);

            if (statusCode >= 200 && statusCode < 300) {
                // 正确的响应。
                return content;
            } else if (statusCode >= 400 && statusCode < 500) {
                // 由于HTTP请求不正确造成的错误，权限不足，登录超时，服务URI错误等等。
                throw new RpcException(request.getURI(), statusCode, reasonPhrase);
            } else if (statusCode >= 500 && statusCode < 600) {
                // 后端的错误。
                throw new RpcException(request.getURI(), statusCode, content);
            } else {
                // 其它不应该出现的状态。
                throw new IllegalStateException("illegal status code: " + statusCode);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } finally {
            logger.trace(loggerBuffer.toString());
        }
    }

    /**
     * 发送HTTP请求并将响应内容写入到指定的流。
     *
     * @param request
     *         待发送的请求。
     * @param destination
     *         写入响应内容的流。
     * @throws IllegalArgumentException
     *         如果参数{@code request}或者{@code destination}是{@code null}。
     * @throws UncheckedIOException
     *         如果从响应中读取数据时出错，或者向参数{@code destination}中写入数据时出错。
     */
    protected final void executeRaw(
            HttpUriRequest request,
            OutputStream destination
    ) {
        notNull(request, "request");
        notNull(destination, "destination");

        final StringBuilder loggerBuffer = new StringBuilder();
        try {
            loggerBuffer.append(SysUtils.format("{} {} ...", request.getMethod(), request.getURI()));
            final HttpResponse response = this.httpClient.execute(request);

            // 删除末尾的省略号。
            loggerBuffer.delete(loggerBuffer.length() - 3, loggerBuffer.length());
            // 然后加入响应状态。
            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            final String reasonPhrase = trimToEmpty(statusLine.getReasonPhrase());
            loggerBuffer.append(SysUtils.format("{} {}", statusCode, reasonPhrase));

            // 解析Response的头部。
            final Date date = getDateHeader(response.getFirstHeader("Date"));
            final long contentLength = getLongHeader(response.getFirstHeader("Content-Length"));
            final String contentType = getStringHeader(response.getFirstHeader("Content-Type"));
            final String[] contentTypeParts = parseContentType(contentType);
            final String mimeType = contentTypeParts[0];
            final String charsetName = contentTypeParts[1];
            final Charset charset = !charsetName.isEmpty() ? Charset.forName(charsetName) : null;

            // 记录History
            final HistoryLevel level = statusCode >= 200 && statusCode < 300 ? HistoryLevel.OK : HistoryLevel.ERROR;

            final History history = new History(date, level, request.getMethod(), request.getURI(), statusCode, contentLength, mimeType, charset, "(bytes)");
            HistoryManager.addHistory(history);

            if (statusCode >= 200 && statusCode < 300) {
                // 正确的响应。
                copy(response.getEntity().getContent(), destination);
            } else {
                // 其它不应该出现的状态。
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } finally {
            logger.trace(loggerBuffer.toString());
        }
    }

    /**
     * 获取字符串形式的头部。
     *
     * @param header
     *         HTTP头部。
     * @return 字符串值，如果参数{@code header}是{@code null}则返回空字符串。
     */
    private String getStringHeader(
            Header header
    ) {
        if (header == null) {
            return "";
        } else {
            return trimToEmpty(header.getValue());
        }
    }

    /**
     * 获取日期形式的头部。
     *
     * @param header
     *         HTTP头部。
     * @return 日期值，如果参数{@code header}是{@code null}则返回{@code null}。
     */
    private Date getDateHeader(
            Header header
    ) {
        if (header == null) {
            return null;
        } else {
            final String value = trimToEmpty(header.getValue());
            if (value.isEmpty()) {
                return null;
            }

            try {
                return parseHttpDate(value);
            } catch (ParseException ex) {
                logger.warn("cannot parse date header[name=" + header.getName() + ",value=" + value + "]", ex);
                return null;
            }
        }
    }

    /**
     * 获取长整数形式的头部。
     *
     * @param header
     *         HTTP头部。
     * @return 长整数值，如果参数{@code header}是{@code null}则返回{@code null}。
     */
    private Long getLongHeader(
            Header header
    ) {
        if (header == null) {
            return null;
        } else {
            final String value = trimToEmpty(header.getValue());
            if (value.isEmpty()) {
                return null;
            }

            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ex) {
                logger.warn("cannot parse long header[name=" + header.getName() + ",value=" + value + "]", ex);
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Result> get(
            String uri,
            Map<String, Object> params
    ) {
        return supplyAsync(() -> prepareRequest(new HttpGet(addParams(fullUri(uri), params))))
                .thenApplyAsync(this::execute, this.executor)
                .thenApplyAsync(DefaultResult::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<byte[]> getRaw(
            String uri,
            Map<String, Object> params
    ) {
        return supplyAsync(() -> prepareRequest(new HttpGet(addParams(fullUri(uri), params))))
                .thenApplyAsync(request -> {
                    // 构造初始大小2KB的缓冲区。
                    try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream(2048)) {
                        executeRaw(request, buffer);
                        return buffer;
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                }, this.executor)
                .thenApplyAsync(ByteArrayOutputStream::toByteArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Result> post(
            String uri,
            Map<String, Object> params
    ) {
        ensureParamsSignature(params);

        return supplyAsync(() -> {
            final HttpPost httpPost = prepareRequest(new HttpPost(fullUri(uri)));
            httpPost.setEntity(paramsToForm(params));
            return httpPost;
        })
                .thenApplyAsync(this::execute, this.executor)
                .thenApplyAsync(DefaultResult::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Result> put(
            String uri,
            Map<String, Object> params
    ) {
        ensureParamsSignature(params);

        return supplyAsync(() -> {
            final HttpPut httpPut = prepareRequest(new HttpPut(fullUri(uri)));
            httpPut.setEntity(paramsToForm(params));
            return httpPut;
        })
                .thenApplyAsync(this::execute, this.executor)
                .thenApplyAsync(DefaultResult::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Result> delete(
            String uri,
            Map<String, Object> params
    ) {
        ensureParamsSignature(params);

        return supplyAsync(() -> prepareRequest(new HttpDelete(addParams(fullUri(uri), params))))
                .thenApplyAsync(this::execute, this.executor)
                .thenApplyAsync(DefaultResult::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
            throws Exception {
        // 关闭执行器并等待2秒钟。
        this.executor.shutdown();
        this.executor.awaitTermination(2, TimeUnit.SECONDS);
    }

    /**
     * 将参数转为可以通过POST或者PUT方式提交的表单。
     * <p>按照{@literal application/x-www-form-urlencoded}的方式对参数进行编码。</p>
     *
     * @param params
     *         参数。
     * @return 可以提交的表单。
     */
    private HttpEntity paramsToForm(
            Map<String, Object> params
    ) {
        if (params == null || params.size() == 0) {
            return EntityBuilder.create()
                                .setContentType(ContentType.APPLICATION_FORM_URLENCODED)
                                .setText("")
                                .build();
        } else {
            return EntityBuilder.create()
                                .setContentType(ContentType.APPLICATION_FORM_URLENCODED)
                                .setParameters(params.entrySet().stream()
                                                     .map(entry -> new BasicNameValuePair(entry.getKey(), paramValueToString(entry.getValue())))
                                                     .toArray(NameValuePair[]::new))
                                .build();
        }
    }
}
