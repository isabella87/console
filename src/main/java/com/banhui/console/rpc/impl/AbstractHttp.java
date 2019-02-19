package com.banhui.console.rpc.impl;

import com.banhui.console.rpc.Http;
import org.apache.http.client.utils.URIBuilder;
import org.xx.armory.commons.SignHelper;
import org.xx.armory.commons.StringEnum;
import org.xx.armory.commons.ValueEnum;
import org.xx.armory.security.KeyStorage;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.xx.armory.commons.SysUtils.stringEquals;
import static org.xx.armory.commons.Validators.notBlank;
import static org.xx.armory.commons.Validators.notNull;

/**
 * HTTP工具类的基础实现。
 */
public abstract class AbstractHttp
        implements Http {
    private static final String SIGNATURE_KEY = "signature";

    private URI baseUri;
    private File cacheDir;

    /**
     * 向URI中加入或者修改查询参数。
     *
     * @param uri
     *         原URI。
     * @param params
     *         参数集合。
     * @return 新的URI。
     */
    protected URI addParams(
            URI uri,
            Map<String, Object> params
    ) {
        notNull(uri, "uri");

        if (params == null) {
            return uri;
        }

        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setCharset(UTF_8)
                  .setScheme(uri.getScheme())
                  .setUserInfo(uri.getUserInfo())
                  .setHost(uri.getHost())
                  .setPort(uri.getPort())
                  .setPath(uri.getPath())
                  .setFragment(uri.getFragment());

        for (final Map.Entry<String, Object> entry : params.entrySet()) {
            uriBuilder.addParameter(entry.getKey(), paramValueToString(entry.getValue()));
        }

        try {
            return uriBuilder.build();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("cannot add parameters to uri: " + uri, ex);
        }
    }

    /**
     * 将参数值转为字符串形式。
     * <p>HTTP协议发送表单时，每个域都必须以文本形式发送，所以需要先将参数值转为字符串。</p>
     * <table>
     * <tr>
     * <th>类型</th>
     * <th>转化方式</th>
     * </tr>
     * <tr>
     * <td>null</td>
     * <td>空字符串({@literal ""})</td>
     * </tr>
     * <tr>
     * <td>{@link CharSequence}</td>
     * <td>调用{@link Object#toString()}方法。</td>
     * </tr>
     * </table>
     *
     * @param value
     *         参数值。
     * @return 字符串形式。
     * @throws IllegalArgumentException
     *         如果参数{@code value}的类型无法识别。
     */
    protected String paramValueToString(
            Object value
    ) {
        if (value == null) {
            return "";
        } else if (value instanceof CharSequence) {
            return value.toString();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "true" : "false";
        } else if (value instanceof Date) {
            return String.valueOf(((Date) value).getTime());
        } else if (value instanceof ValueEnum) {
            return String.valueOf(((ValueEnum) value).longValue());
        } else if (value instanceof StringEnum) {
            return ((StringEnum) value).stringValue();
        } else if (value.getClass().isArray()) {
            final StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < Array.getLength(value); ++i) {
                buffer.append(paramValueToString(Array.get(value, i)));
            }
            return buffer.toString();
        } else {
            throw new IllegalArgumentException("cannot convert parameter " + value + " to string");
        }
    }

    /**
     * 获取基础URI。
     *
     * @return 基础URI。
     */
    public final URI getBaseUri() {
        return baseUri;
    }

    /**
     * 设置基础URI。
     *
     * @param baseUri
     *         基础URI。
     * @throws IllegalArgumentException
     *         如果参数{@code baseUri}是{@code null}，或者不是绝对URI。
     */
    public void setBaseUri(
            URI baseUri
    ) {
        notNull(baseUri, "baseUri");
        if (!baseUri.isAbsolute()) {
            throw new IllegalArgumentException("base uri " + baseUri + " cannot be relative");
        }

        this.baseUri = baseUri;
    }

    /**
     * 获取保存缓存文件的目录。
     *
     * @return 保存缓存文件的目录。
     */
    public final File getCacheDir() {
        return this.cacheDir;
    }

    /**
     * 设置保存缓存文件的目录。
     *
     * @param cacheDir
     *         保存缓存文件的目录。
     */
    public final void setCacheDir(
            File cacheDir
    ) {
        this.cacheDir = cacheDir;
    }

    /**
     * 确保参数的完整性签名正确。
     * <p>将所有的参数转为字符串形式并连接起来，通过私钥对连接结果进行签名并填入{@link #SIGNATURE_KEY}字段。</p>
     *
     * @param params
     *         待检查的参数。
     */
    protected void ensureParamsSignature(
            Map<String, Object> params
    ) {
        if (params != null) {
            SignHelper signHelper = SignHelper.getInstance();
            final String paramsString = params.entrySet().stream()
                                              .filter(entry -> !stringEquals(entry.getKey(), SIGNATURE_KEY))
                                              .map(Map.Entry::getValue)
                                              .map(this::paramValueToString)
                                              .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                                              .toString();

            try {
                params.put(SIGNATURE_KEY, signHelper.signBase64(paramsString, KeyStorage.getInstance().getRSAPrivateKey("verify-params-private")));
            } catch (GeneralSecurityException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    /**
     * 通过{@link #baseUri}将相对路径转化为绝对路径。
     *
     * @param uri
     *         原路径。
     * @return 如果原路径是绝对路径，那么直接返回原路径，否则通过基础URI将此路径转化为绝对路径。
     * @throws IllegalArgumentException
     *         如果参数{@code uri}是{@code null}或者只包含空白字符。
     */
    protected URI fullUri(
            String uri
    ) {
        uri = notBlank(uri, "uri").trim();

        final URI uri_ = URI.create(uri);
        return uri_.isAbsolute() ? uri_ : baseUri.resolve(uri_);
    }
}
