package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BaPrjGuaranteePersProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的担保个人。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的担保个人列表。
     */
    public CompletableFuture<Result> all(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/ba/prj-guarantee-persons", params);
    }

    public CompletableFuture<Result> add(
            Map<String, Object> params
    ) {
        return super.http().put("mgr/ba/prj-guarantee-persons", params);
    }

    public CompletableFuture<Result> update(long id,
            Map<String, Object> params
    ) {
        return super.http().post("mgr/ba/prj-guarantee-persons/"+id, params);
    }

    public CompletableFuture<Result> del(long id
    ) {
        return super.http().delete("mgr/ba/prj-guarantee-persons/" + id, null);
    }

    public CompletableFuture<Result> query(long id
    ) {
        return super.http().get("mgr/ba/prj-guarantee-persons/"+ id,null);
    }

}
