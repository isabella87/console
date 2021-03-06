package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BaPrjGuaranteeOrgsProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的担保机构。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的担保机构列表。
     */
    public CompletableFuture<Result> all(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/ba/prj-guarantee-orgs", params);
    }

    public CompletableFuture<Result> add(
            Map<String, Object> params
    ) {
        return super.http().put("mgr/ba/prj-guarantee-orgs", params);
    }

    public CompletableFuture<Result> update(
            Map<String, Object> params
    ) {
        final long id = takeLong(params, "id");
        return super.http().post("mgr/ba/prj-guarantee-orgs/" + id, params);
    }

    public CompletableFuture<Result> del(
            long id
    ) {
        return super.http().delete("mgr/ba/prj-guarantee-orgs/" + id, null);
    }

    public CompletableFuture<Result> query(
            long id
    ) {
        return super.http().get("mgr/ba/prj-guarantee-orgs/" + id, null);
    }

}
