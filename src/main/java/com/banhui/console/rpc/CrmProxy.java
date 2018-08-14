package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CrmProxy
        extends AbstractProxy {
    /**
     * 客户关系管理
     *
     * @param params
     *         查询条件。
     * @return 符合条件的列表。
     */
    //分配注册客户
    public CompletableFuture<Result> queryRegUsers(Map<String, Object> params) {
        return super.http().get("crm/reg-investors/relations", params);
    }

    public CompletableFuture<Result> getAllMgrRelations(Map<String, Object> params) {
        return super.http().get("crm/cm/all-relations", params);
    }

    public CompletableFuture<Result> queryHistoryMgrs(long auId) {
        return super.http().get("crm/reg-investors/" + auId + "/history-relations", null);
    }

    public CompletableFuture<Result> bindRegUserWithMgr(Map<String, Object> params) {
        return super.http().post("crm/reg-investors/bind", params);
    }

    public CompletableFuture<Result> queryNewRegs(Map<String, Object> params) {
        return super.http().get("crm/reg-investors/new-regs", params);
    }

    public CompletableFuture<Result> createAssign(Map<String, Object> params) {
        return super.http().post("crm/reg-investors/assign", params);
    }

    //我的注册客户
    public CompletableFuture<Result> myRegUsers(Map<String, Object> params) {
        return super.http().get("crm/reg-investors/my", params);
    }

    public CompletableFuture<Result> myRegUsersById(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("crm/reg-investors/" + auId, params);
    }

    /**
     * 客户经理层级相关
     */
    public CompletableFuture<Result> createCrmMgrRelation(Map<String, Object> params) {
        return super.http().put("crm/cm", params);
    }

    public CompletableFuture<Result> getCrmMgrRelation(String uname) {
        return super.http().get("crm/cm/" + uname, null);
    }

    public CompletableFuture<Result> updateCrmMgrRelation(Map<String, Object> params) {
        String uname = takeString(params, "u-name");
        return super.http().post("crm/cm/" + uname, params);
    }

    public CompletableFuture<Result> deleteCrmMgrRelation(String uname) {
        return super.http().delete("crm/cm/" + uname, null);
    }

    public CompletableFuture<Result> moveCrmMgrRelation(Map<String, Object> params) {
        String uname = takeString(params, "u-name");
        return super.http().post("crm/cm/" + uname + "/position", params);
    }

    public CompletableFuture<Result> updateCrmMgrRcode(Map<String, Object> params) {
        String uname = takeString(params, "u-name");
        return super.http().post("crm/cm/" + uname + "/r-code", params);
    }
}
