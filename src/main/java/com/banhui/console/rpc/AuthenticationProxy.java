package com.banhui.console.rpc;


import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 用户授权相关的代理。
 */
public final class AuthenticationProxy
        extends AbstractProxy
        implements Proxy {
    /**
     * 登录到后台系统。
     *
     * @param params
     *         登录的参数，包括登录名，口令和验证码。
     * @return 响应结果。
     */
    public CompletableFuture<Result> signIn(
            Map<String, Object> params
    ) {
        return http().post("/p2psrv/security/signin", params);
    }

    public CompletableFuture<byte[]> captchaImage() {
        return http().getRaw("/p2psrv/security/captcha-image", null);
    }

    /**
     * 获取当前登录用户的信息。
     *
     * @return 当前登录用户的信息。
     */
    public CompletableFuture<Result> current() {
        return http().get("current", null);
    }
}
