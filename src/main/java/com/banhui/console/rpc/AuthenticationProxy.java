package com.banhui.console.rpc;


import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.xx.armory.commons.Validators.notEmpty;

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
        return http().post("security/signin", params);
    }

    public CompletableFuture<byte[]> captchaImage() {
        return http().getRaw("security/captcha-image", null);
    }

    /**
     * 获取当前登录用户的信息。
     *
     * @return 当前登录用户的信息。
     */
    public CompletableFuture<Result> current() {
        return http().get("security/user", null);
    }

    /**
     * 修改当前登录用户的密码。
     * @param params 修改密码的参数。
     * @return 响应结果。
     */
    public CompletableFuture<Result> changePassword(
            Map<String, Object> params
    ) {
        return http().post("security/change-pwd", params);
    }
}
