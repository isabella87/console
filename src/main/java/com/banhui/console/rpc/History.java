package com.banhui.console.rpc;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.xx.armory.commons.SysUtils.format;
import static org.xx.armory.commons.Validators.greaterThanOrEqual;
import static org.xx.armory.commons.Validators.notBlank;
import static org.xx.armory.commons.Validators.notNull;

/**
 * RPC请求和响应的历史。
 */
public final class History {
    private final Date date;
    private final HistoryLevel level;
    private final String verb;
    private final URI uri;
    private final int responseCode;
    private final Long responseLength;
    private final String responseMimeType;
    private final Charset responseCharset;
    private final String responseDigest;

    /**
     * 构造一条请求和响应的历史记录。
     *
     * @param level
     *         历史记录的级别。
     * @param uri
     *         请求的URI。
     * @param responseCode
     *         响应码，比如200表示响应成功，404表示请求的URI找不到等等。
     * @param responseLength
     *         响应内容的长度。
     * @param responseMimeType
     *         响应内容的类型。
     * @param responseCharset
     *         响应内容的编码。
     * @param responseDigest
     *         响应内容的开头部分。
     * @throws IllegalArgumentException
     *         如果参数{@code date}是{@code null}，或者参数{@code verb}是{@code null}或者只包含空白字符，或者参数{@code uri}是{@code null}，或者参数{@code responseCode}小于{@code 200}，或者参数{@code responseMimeType}是{@code null}。
     */
    public History(
            Date date,
            HistoryLevel level,
            String verb,
            URI uri,
            int responseCode,
            Long responseLength,
            String responseMimeType,
            Charset responseCharset,
            String responseDigest
    ) {
        this.date = notNull(date, "date");
        this.level = level;
        this.verb = notBlank(verb, "verb").trim().toUpperCase();
        this.uri = notNull(uri, "uri");
        this.responseCode = greaterThanOrEqual(responseCode, "responseCode", 200);
        this.responseLength = responseLength;
        this.responseMimeType = notNull(responseMimeType, "responseMimeType");
        this.responseCharset = responseCharset;
        this.responseDigest = trimToEmpty(responseDigest);
    }

    /**
     * 获得HTTP请求的时间点。
     *
     * @return HTTP请求的时间点。
     */
    public final Date getDate() {
        return this.date;
    }

    /**
     * 获取历史记录的级别。
     *
     * @return 历史记录的级别。
     */
    public final HistoryLevel getLevel() {
        return level;
    }

    /**
     * 获得请求的HTTP方法。
     *
     * @return 请求的HTTP方法。
     */
    public final String getVerb() {
        return this.verb;
    }

    /**
     * 获取请求的URI。
     *
     * @return 请求的URI。
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * 获取响应码。
     *
     * @return 响应码。
     */
    public final int getResponseCode() {
        return this.responseCode;
    }

    /**
     * 获取响应内容的长度。
     *
     * @return 响应内容的长度。
     */
    public final Long getResponseLength() {
        return this.responseLength;
    }

    /**
     * 获取响应内容的类型。
     *
     * @return 响应内容的类型。
     */
    public final String getResponseMimeType() {
        return this.responseMimeType;
    }

    /**
     * 获取响应内容的编码。
     *
     * @return 响应内容的编码，{@code null}表示响应内容不是文本，所以不需要指定编码。
     */
    public final Charset getResponseCharset() {
        return this.responseCharset;
    }

    /**
     * 获取响应内容的开头部分。
     *
     * @return 响应内容的开头部分。
     */
    public String getResponseDigest() {
        return this.responseDigest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return format("{} {} {} {} {} {} {}", this.verb, this.uri, this.responseCode,
                      this.responseMimeType != null ? this.responseMimeType : "<unknown>",
                      this.responseCharset != null ? this.responseCharset.toString() : "<unknown>",
                      this.responseLength != null ? this.responseLength.toString() : "<unknown>",
                      replace(replace(this.responseDigest, "\r", ""), "\n", " "));
    }
}
