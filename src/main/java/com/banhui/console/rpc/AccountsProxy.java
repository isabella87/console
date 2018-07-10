package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AccountsProxy
        extends AbstractProxy {
    /**
     * 平台注册用户管理
     *
     * @param params
     *         查询条件。
     * @return 符合条件的列表。
     */

    public CompletableFuture<Result> queryAccOrgInfo(Map<String, Object> params) {
        return super.http().get("mgr/accounts/orgs", params);
    }

    public CompletableFuture<Result> queryAccOrgInfoById(long id) {
        return super.http().get("mgr/accounts/orgs/" + id, null);
    }
}
