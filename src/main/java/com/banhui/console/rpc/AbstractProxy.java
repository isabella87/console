package com.banhui.console.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.xx.armory.commons.Validators.notBlank;

public abstract class AbstractProxy
        implements Proxy {
    private final Logger logger = LoggerFactory.getLogger(AbstractProxy.class);

    protected final Http http() {
        return HttpManager.getHttp();
    }

    protected void addHistory(
            Result result,
            Throwable throwable
    ) {
        if (throwable != null) {
            // 后端调用时发生了异常。
            if (throwable instanceof Error) {
                // 错误，应当立刻退出应用程序。
                logger.error("system handle", throwable);
                System.exit(1);
            }

            if (throwable instanceof ExecutionException) {
                throwable = throwable.getCause();
            }
            if (throwable instanceof InvocationTargetException) {
                throwable = throwable.getCause();
            }

            if (throwable instanceof RpcException) {
                // Rpc异常，应当记录到历史中。
            } else if (throwable instanceof RuntimeException) {
                // 其它运行时异常，应当重新抛出。
                throw (RuntimeException) throwable;
            } else {
                // 受检异常，应当包装后抛出。
                throw new IllegalStateException(throwable);
            }
        } else {
            // 后端调用未发生异常。
        }
    }

    /**
     * 从参数集合中提取长整数参数。
     *
     * @param key
     *         参数名，自动去掉首尾空格。
     * @param params
     *         参数集合。
     * @return 长整数参数的值。
     * @throws IllegalArgumentException
     *         如果参数{@code key}是{@code null}或者只包含空白字符，如果参数{@code params}是{@code null}，
     *         如果参数集合中存在指定名字的参数，但是无法被转化为长整数。
     * @throws NumberFormatException
     *         如果参数集合中存在指定名字的参数，并且参数值是字符串，但是无法被解析为长整数。
     */
    protected long takeLong(
            Map<String, Object> params,
            String key
    ) {
        key = notBlank(key, "key").trim();
        final Object obj = params.remove(key);
        if (obj == null) {
            throw new IllegalArgumentException("cannot take parameter: " + key);
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof String) {
            return Long.parseLong((String) obj);
        } else {
            throw new IllegalArgumentException("cannot take parameter: " + key + ", actual value: " + obj);
        }
    }

    /**
     * 从参数集合中提取整数参数。
     *
     * @param key
     *         参数名，自动去掉首尾空格。
     * @param params
     *         参数集合。
     * @return 整数参数的值。
     * @throws IllegalArgumentException
     *         如果参数{@code key}是{@code null}或者只包含空白字符，如果参数{@code params}是{@code null}，
     *         如果参数集合中存在指定名字的参数，但是无法被转化为整数。
     * @throws NumberFormatException
     *         如果参数集合中存在指定名字的参数，并且参数值是字符串，但是无法被解析为整数。
     */
    protected int takeInt(
            Map<String, Object> params,
            String key
    ) {
        key = notBlank(key, "key").trim();
        final Object obj = params.remove(key);
        if (obj == null) {
            throw new IllegalArgumentException("cannot take parameter: " + key);
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            return toInt((String) obj);
        } else {
            throw new IllegalArgumentException("cannot take parameter: " + key + ", actual value: " + obj);
        }
    }

    /**
     * 从参数集合中提取字符串参数。
     *
     * @param key
     *         参数名，自动去掉首尾空格。
     * @param params
     *         参数集合。
     * @return 字符串参数的值，如果参数集合中不包含指定名字的参数则返回空字符串。
     * @throws IllegalArgumentException
     *         如果参数{@code key}是{@code null}或者只包含空白字符，如果参数{@code params}是{@code null}。
     */
    protected String takeString(
            Map<String, Object> params,
            String key
    ) {
        key = notBlank(key, "key").trim();
        final Object obj = params.remove(key);
        return obj != null ? obj.toString() : "";
    }
}
