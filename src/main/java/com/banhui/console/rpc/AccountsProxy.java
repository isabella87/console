package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AccountsProxy
        extends AbstractProxy {
    /**
     * 平台注册用户管理
     *
     * @param params
     *         查询条件。
     * @return 符合条件的列表。
     */

    //机构账号
    public CompletableFuture<Result> queryAccOrgInfo(Map<String, Object> params) {
        return super.http().get("mgr/accounts/orgs", params);
    }

    public CompletableFuture<Result> queryAccOrgInfoById(long id) {
        return super.http().get("mgr/accounts/orgs/" + id, null);
    }

    public CompletableFuture<Result> updateOrgInfo(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().post("mgr/accounts/orgs/" + auId + "/user-info", params);
    }

    public CompletableFuture<Result> setAllowRoles(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().post("mgr/accounts/" + auId + "/allow-roles", params);
    }

    public CompletableFuture<Result> getAllowRoles(long auId) {
        return super.http().get("mgr/accounts/" + auId + "/allow-roles", null);
    }

    public CompletableFuture<Result> updateLevel(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().post("mgr/accounts/" + auId + "/level", params);
    }

    public CompletableFuture<Result> getJxWithdrawList(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/" + auId + "/jx-withdraw", params);
    }

    public CompletableFuture<Result> getJxTenderList(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/" + auId + "/jx-tender", params);
    }

    public CompletableFuture<Result> getJxCreditAssignList(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/" + auId + "/jx-credit-assign", params);
    }

    public CompletableFuture<Result> bankFundsDetail(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/third-user-info/" + auId + "/bank-all-funds-detail", params);
    }

    public CompletableFuture<Result> lock(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().post("mgr/accounts/" + auId + "/lock", params);
    }

    public CompletableFuture<Result> historyFundsDeposit(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/third-user-info/" + auId + "/funds-details-latest", params);
    }

    public CompletableFuture<Result> historyFundsPlatform(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/third-user-info/" + auId + "/history-funds-detail", params);
    }

    public CompletableFuture<Result> historyInvests(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/invest/" + auId + "/history-invests", params);
    }

    public CompletableFuture<Result> borrowRecord(Map<String, Object> params) {
        long auId = takeLong(params, "id");
        return super.http().get("mgr/accounts/borrow/" + auId + "/borrows", params);
    }

    public CompletableFuture<Result> monthReports(long auId) {
        return super.http().get("mgr/accounts/" + auId + "/month-reports", null);
    }

    public CompletableFuture<Result> tsTendersDeposit(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/third-user-info/" + auId + "/credit-detail", params);
    }

    public CompletableFuture<Result> tsTendersPlatform(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/" + auId + "/ts-tender", params);
    }

    public CompletableFuture<Result> frozenFund(long auId) {
        return super.http().get("mgr/accounts/third-user-info/" + auId + "/balance", null);
    }

    public CompletableFuture<Result> frozenDetail(Map<String, Object> params) {
        return super.http().get("mgr/trans/balance-frz", params);
    }

    public CompletableFuture<Result> unfrozen(Map<String, Object> params) {
        return super.http().post("mgr/trans/balance-unfrz", params);
    }

    public CompletableFuture<Result> confirmRegistry(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().post("mgr/accounts/orgs/" + auId + "/registry", params);
    }

    public CompletableFuture<Result> bankOrgInfo(long auId) {
        return super.http().get("mgr/accounts/orgs/" + auId + "/jx-userinfo", null);
    }

    //个人账号
    public CompletableFuture<Result> queryAccPersonInfos(Map<String, Object> params) {
        return super.http().get("mgr/accounts/persons", params);
    }

    public CompletableFuture<Result> getAccPersonTotal(Map<String, Object> params) {
        return super.http().get("mgr/accounts/persons-total", params);
    }

    public CompletableFuture<Result> queryAccPersonInfoById(long id) {
        return super.http().get("mgr/accounts/persons/" + id, null);
    }

    public CompletableFuture<Result> getJxRechargeList(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().get("mgr/accounts/" + auId + "/jx-recharge", params);
    }

    public CompletableFuture<Result> checkRegistry(Map<String, Object> params) {
        long auId = takeLong(params, "au-id");
        return super.http().post("mgr/accounts/third-user-info/" + auId + "/check-person-registry", params);
    }

    public CompletableFuture<Result> bankPerInfo(long auId) {
        return super.http().get("mgr/accounts/persons/" + auId + "/jx-userinfo", null);
    }

    //通过账户查看自动投标情况记录
    public CompletableFuture<Result> accAutoTender(Map<String, Object> params) {
        return super.http().get("mgr/accounts/acc-auto-tender-log" ,params);
    }
}
