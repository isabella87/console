package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BaPrjMortgageProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询质押人。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的质押人。
     */
    public CompletableFuture<Result> all(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/ba/prj-mortgage", params);
    }

    public CompletableFuture<Result> add(
            Map<String, Object> params
    ) {
        return super.http().put("mgr/ba/prj-mgr-mortgage", params);
    }

    public CompletableFuture<Result> update(long id,
            Map<String, Object> params
    ) {
        return super.http().post("mgr/ba/prj-mgr-mortgage/"+id, params);
    }

    public CompletableFuture<Result> del(long id
    ) {
        return super.http().delete("mgr/ba/prj-mgr-mortgage/" + id, null);
    }

    public CompletableFuture<Result> query(long id
    ) {
        return super.http().get("mgr/ba/prj-mgr-mortgage/"+ id,null);
    }


}
