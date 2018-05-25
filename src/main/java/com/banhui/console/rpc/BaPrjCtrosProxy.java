package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BaPrjCtrosProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的施工单位。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的施工单位列表。
     */
    public CompletableFuture<Result> all(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/ba/prj-ctors", params);
    }

    public CompletableFuture<Result> add(
            Map<String, Object> params
    ) {
        return super.http().put("mgr/ba/prj-ctors", params);
    }

    public CompletableFuture<Result> update(long id,
            Map<String, Object> params
    ) {
        return super.http().post("mgr/ba/prj-ctors/"+id, params);
    }

    public CompletableFuture<Result> del(long id
    ) {
        return super.http().delete("mgr/ba/prj-ctors/" + id, null);
    }

    public CompletableFuture<Result> query(long id
    ) {
        return super.http().get("mgr/ba/prj-ctors/"+ id,null);
    }

}
