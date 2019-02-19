package com.banhui.console.rpc;


import com.banhui.console.rpc.impl.DefaultHttp;
import org.xx.armory.bindings.Param;
import org.xx.armory.swing.Application;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.xx.armory.commons.Validators.notEmpty;

/**
 * 用户授权相关的代理。
 */
public final class AuthenticationProxy
        extends AbstractProxy
        implements Proxy {

    public void resetHttpBaseUrl(){
        DefaultHttp defaultHttp = ((DefaultHttp)http());
        defaultHttp.setBaseUri(URI.create(Application.settings().getProperty("rpc-base-uri")));
    }
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
        String aa = Application.settings().getProperty("rpc-base-uri");
        System.out.println("***********************************"+aa);
        return http().post("security/signin", params);
    }

    public CompletableFuture<Result> signOut(){
        return http().post("security/signOut",null);
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
     *
     * @param params
     *         修改密码的参数。
     * @return 响应结果。
     */
    public CompletableFuture<Result> changePassword(
            Map<String, Object> params
    ) {
        return http().post("security/change-pwd", params);
    }

    /**
     * 检查当前登录用户的口令是否匹配
     *
     * @return 登录名和口令是否匹配, 如果不存在当前登录用户那么也不匹配
     */
    public CompletableFuture<Result> validateUser(
            Map<String, Object> params
    ) {
        return http().get("security/validate-user", params);
    }
}
