package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BaPrjOwnersProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的项目业主。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的项目业主列表。
     */
    public CompletableFuture<Result> all(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/ba/prj-owners", params);
    }

    public CompletableFuture<Result> add(
            Map<String, Object> params
    ) {
        return super.http().put("mgr/ba/prj-owners", params);
    }

    public CompletableFuture<Result> update(long id,
            Map<String, Object> params
    ) {
        return super.http().post("mgr/ba/prj-owners/"+id, params);
    }

    public CompletableFuture<Result> del(long id
    ) {
        return super.http().delete("mgr/ba/prj-owners/" + id, null);
    }

    public CompletableFuture<Result> query(long id
    ) {
        return super.http().get("mgr/ba/prj-owners/"+ id,null);
    }
}
