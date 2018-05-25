package com.banhui.console.rpc;

import com.banhui.console.rpc.impl.LocalArrayResult;
import com.banhui.console.rpc.impl.LocalMapResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.xx.armory.commons.Validators.notBlank;

/**
 * 后台管理类型。
 */
public class SysProxy
        extends AbstractProxy {
    /**
     * 查询所有符合条件的后台帐户。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的记录列表。
     */
    public CompletableFuture<Result> allUsers(
            Map<String, Object> params
    ) {
        return http().get("mgr/sys-users", params);
    }

    public CompletableFuture<Result> userByName(
            String userName
    ) {
        userName = trimToEmpty(userName);
        if (userName.isEmpty()) {
            return CompletableFuture.completedFuture(new LocalMapResult());
        } else {
            return http().get("mgr/sys-users/" + userName, null);
        }
    }

    public CompletableFuture<Result> rolesByUserName(
            String userName
    ) {
        userName = trimToEmpty(userName);
        if (userName.isEmpty()) {
            return CompletableFuture.completedFuture(new LocalArrayResult());
        } else {
            return http().get("mgr/sys-users/" + userName + "/roles", null);
        }
    }

    /**
     * 查询所有符合条件的后台角色。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的记录列表。
     */
    public CompletableFuture<Result> allRoles(
            Map<String, Object> params
    ) {
        return http().get("mgr/sys-roles", params);
    }

    public CompletableFuture<Result> update(
            Map<String, Object> params
    ) {
        final Object userName = params.remove("user-name");
        return http().post("mgr/sys-users/" + userName, params);
    }

    public CompletableFuture<Result> add(
            Map<String, Object> params
    ) {
        return http().put("mgr/sys-users", params);
    }

    public CompletableFuture<Result> delete(
            String userName
    ) {
        userName = notBlank(userName, "userName").trim();
        return http().delete("mgr/sys-users/" + userName, null);
    }

    /**
     * 重置指定后台帐户的密码。
     *
     * @param userName
     *         后台帐户的登录名。
     * @return 执行结果。
     */
    public CompletableFuture<Result> resetPassword(
            String userName
    ) {
        return http().post("mgr/sys-users/" + userName + "/reset-pwd", null);
    }
}
