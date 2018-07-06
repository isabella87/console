package com.banhui.console.rpc;

import com.banhui.console.rpc.AbstractProxy;
import com.banhui.console.rpc.Result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CreditAssignmentsProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的债权转让项目。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的列表。
     */
    public CompletableFuture<Result> queryAll(Map<String, Object> params) {
        return super.http().get("mgr/prj/credit-assign", params);
    }

    public CompletableFuture<Result> revoke(long pid) {
        return super.http().post("mgr/prj/credit-assign/" + pid, null);
    }
}
