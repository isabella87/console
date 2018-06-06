package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProjectProxy
        extends AbstractProxy {
    /**
     * 按照指定的查询条件查询所有的贷款项目。
     *
     * @param params
     *         查询条件。
     * @return 符合条件的贷款项目列表。
     */
    public CompletableFuture<Result> allProjects(
            Map<String, Object> params
    ) {
        return super.http().get("mgr/prj/loan-projs", params);
    }

    //查询投标信息
    public CompletableFuture<Result> queryTenders(
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/tenders", null);
    }

    //撤销投标
    public CompletableFuture<Result> createTsCancelTender(
            long tt_id,
            Map<String, Object> params
    ) {
        return super.http().put("mgr/trans/tenders/" + tt_id + "/cancellation", params);
    }

    //执行流标（查询）
    public CompletableFuture<Result> queryCancelTenders(
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/cancel-tenders", null);
    }

    //执行流标
    public CompletableFuture<Result> execute(
            long tct_id
    ) {
        return super.http().post("mgr/trans/cancel-tender/" + tct_id + "/execution", null);
    }

    //查看放款记录
    public CompletableFuture<Result> queryLoans(
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/loans", null);
    }

    //执行放款
    public CompletableFuture<Result> executeLoan(
            long id
    ) {
        return super.http().post("mgr/prj/" + id + "/batch-loan", null);
    }

    //出借人信息表
    public CompletableFuture<Result> queryInvestorInfosByPId(
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/investors", null);
    }

    //有效投资
    public CompletableFuture<Result> queryInvests(
            Map<String, Object> params,
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/invests", params);
    }

    //中途流标
    public CompletableFuture<Result> busVpStopRaising(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/bus-vp-stop-raising", params);
    }

    public CompletableFuture<Result> prjBonus(
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/bonus", null);
    }

    //结清
    public CompletableFuture<Result> completedPrj(
            long id
    ) {
        return super.http().post("mgr/prj/" + id + "/completed", null);
    }

    //锁定
    public CompletableFuture<Result> lockPrj(
            long id
    ) {
        return super.http().post("mgr/prj/" + id + "/prj-lock", null);
    }

    //解锁
    public CompletableFuture<Result> unlockPrj(
            long id
    ) {
        return super.http().post("mgr/prj/" + id + "/prj-unlock", null);
    }

    //锁定状态
    public CompletableFuture<Result> prjLockStatus(
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/prj-lock-status", null);
    }

    //查询审批列表
    public CompletableFuture<Result> queryAudit(
            long id
    ) {
        return super.http().get("mgr/prj/" + id + "/actions", null);
    }

    //执行各个审批步骤
    public CompletableFuture<Result> prjSubmit(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/submittal", params);
    }

    public CompletableFuture<Result> prjMgrAudit(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/prj-mgr-audit", params);
    }

    public CompletableFuture<Result> riskCtrlAudit(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/risk-ctrl-audit", params);
    }

    public CompletableFuture<Result> busSecAudit(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/bus-sec-audit", params);
    }

    public CompletableFuture<Result> busVpAprOnLine(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/bus-vp-apr-online", params);
    }

    public CompletableFuture<Result> busVpConfirmFull(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/bus-vp-confirm-full", params);
    }

    public CompletableFuture<Result> checkFee(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/check-fee", params);
    }

    public CompletableFuture<Result> busVpConfirmLoan(
            Map<String, Object> params,
            long id
    ) {
        return super.http().post("mgr/prj/loan-projs/" + id + "/bus-vp-confirm-loan", params);
    }
}
