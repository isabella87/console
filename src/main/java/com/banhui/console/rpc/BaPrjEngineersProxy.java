package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BaPrjEngineersProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的项目工程。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的项目工程列表。
     */
    public CompletableFuture<Result> all(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/ba/prj-engineers", params);
    }

    public CompletableFuture<Result> add(
            Map<String, Object> params
    ) {
        return super.http().put("mgr/ba/prj-engineers", params);
    }

    public CompletableFuture<Result> update(long id,
                                            Map<String, Object> params
    ) {
        return super.http().post("mgr/ba/prj-engineers/"+id, params);
    }

    public CompletableFuture<Result> del(long id
    ) {
        return super.http().delete("mgr/ba/prj-engineers/" + id, null);
    }

    public CompletableFuture<Result> query(long id
    ) {
        return super.http().get("mgr/ba/prj-engineers/"+ id,null);
    }

}
