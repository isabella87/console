package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProjectRepayProxy
        extends AbstractProxy {

    public CompletableFuture<Result> queryPrjBonus(long pId) {
        return super.http().get("mgr/prj/" + pId + "/bonus", null);
    }

    public CompletableFuture<Result> queryPrjBonusDetail(long pId) {
        return super.http().get("mgr/prj/" + pId + "/bonus-details", null);
    }

    public CompletableFuture<Result> createPrjBonusDetail(Map<String,Object> params){
        long pId = takeLong(params,"pId");
        return super.http().put("mgr/prj/loan-projs/"+pId+"/bonus-details",params);
    }

    public CompletableFuture<Result> delPrjBonusDetail(Map<String,Object> params) {
        long pId = takeLong(params,"pId");
        long pbdId = takeLong(params,"pbdId");
        return super.http().delete("mgr/prj/" + pId + "/bonus-details/"+pbdId, null);
    }

    public CompletableFuture<Result> queryBonusDetailRepays(Map<String,Object> params){
        long pId = takeLong(params,"pId");
        long pbdId = takeLong(params,"pbdId");
        return super.http().get("mgr/prj/" + pId + "/bonus-details/" + pbdId + "/repays",null);
    }

    public CompletableFuture<Result> executeRepay(Map<String,Object> params){
        long pId = takeLong(params,"pId");
        long pbdId = takeLong(params,"pbdId");
        return super.http().post("mgr/prj/" + pId + "/batch-repay/" + pbdId,params);
    }
}
