package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DisclosureProxy
        extends AbstractProxy {
    /**
     * 信息披露
     *
     * @param params
     *         查询条件。
     * @return 符合条件的列表。
     */

    public CompletableFuture<Result> queryList(Map<String, Object> params) {
        return super.http().get("mgr/disclosure/disclosure-info-list", params);
    }

    public CompletableFuture<Result> infoById(Map<String, Object> params) {
        return super.http().get("mgr/disclosure/disclosure-info", params);
    }

    public CompletableFuture<Result> updateOverviews(Map<String, Object> params) {
        return super.http().post("mgr/disclosure/update-overviews", params);
    }

    public CompletableFuture<Result> createOverview(Map<String, Object> params) {
        return super.http().post("mgr/disclosure/overviews", params);
    }

    public CompletableFuture<Result> deleteDisclosure(Map<String, Object> params) {
        return super.http().delete("mgr/disclosure/delete-disclosure", params);
    }

    public CompletableFuture<Result> detail(Map<String, Object> params) {
        return super.http().get("mgr/disclosure/detail", params);
    }

    public CompletableFuture<Result> createDetail(Map<String, Object> params) {
        return super.http().post("mgr/disclosure/detail", params);
    }

    public CompletableFuture<Result> updateDetail(Map<String, Object> params) {
        return super.http().post("mgr/disclosure/update-detail", params);
    }
}
