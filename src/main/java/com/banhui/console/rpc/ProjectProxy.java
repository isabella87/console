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
    public CompletableFuture<Result> allProjects(Map<String, Object> params) {
        return super.http().get("mgr/prj/loan-projs", params);
    }

    public CompletableFuture<Result> queryPrjLoanById(long id) {
        return super.http().get("mgr/prj/loan-projs/" + id, null);
    }

    public CompletableFuture<Result> deletePrjLoan(long id) {
        return super.http().delete("mgr/prj/loan-projs/" + id, null);
    }

    public CompletableFuture<Result> createPrjLoan(Map<String, Object> params) {
        return super.http().put("mgr/prj/loan-projs", params);
    }

    public CompletableFuture<Result> updatePrjLoan(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().post("mgr/prj/loan-projs/" + pid, params);
    }

    public CompletableFuture<Result> queryFinancierById(long id) {
        return super.http().get("mgr/prj/loan-projs/" + id + "/financier", null);
    }

    public CompletableFuture<Result> queryPrjGuaranteePersons(long id) {
        return super.http().get("mgr/prj/" + id + "/guarantee-person", null);
    }

    public CompletableFuture<Result> createPrjGuaranteePerson(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().put("mgr/prj/" + pid + "/guarantee-person", params);
    }

    public CompletableFuture<Result> deletePrjGuaranteePerson(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        long pgpId = takeLong(params, "pgp-id");
        return super.http().delete("mgr/prj/" + pid + "/guarantee-person/" + pgpId, params);
    }

    public CompletableFuture<Result> queryPrjGuaranteeOrg(long id) {
        return super.http().get("mgr/prj/" + id + "/guarantee-org", null);
    }

    public CompletableFuture<Result> createPrjGuaranteeOrg(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().put("mgr/prj/" + pid + "/guarantee-org", params);
    }

    public CompletableFuture<Result> deletePrjGuaranteeOrg(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        long pgoId = takeLong(params, "pgo-id");
        return super.http().delete("mgr/prj/" + pid + "/guarantee-org/" + pgoId, params);
    }

    public CompletableFuture<Result> queryBorPersons(long id) {
        return super.http().get("mgr/prj/" + id + "/mgr-person", null);
    }

    public CompletableFuture<Result> createBorPerson(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().put("mgr/prj/" + pid + "/mgr-person", params);
    }

    public CompletableFuture<Result> deleteBorPerson(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        long bpmpId = takeLong(params, "bpmp-id");
        return super.http().delete("mgr/prj/" + pid + "/mgr-person/" + bpmpId, params);
    }

    public CompletableFuture<Result> queryBorOrgs(long id) {
        return super.http().get("mgr/prj/" + id + "/mgr-org", null);
    }

    public CompletableFuture<Result> createBorOrg(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().put("mgr/prj/" + pid + "/mgr-org", params);
    }

    public CompletableFuture<Result> deleteBorOrg(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        long bpmoId = takeLong(params, "bpmo-id");
        return super.http().delete("mgr/prj/" + pid + "/mgr-org/" + bpmoId, params);
    }

    public CompletableFuture<Result> prjMgrPersonsByPrjId(long id) {
        return super.http().get("mgr/prj/" + id + "/mgr-person", null);
    }

    public CompletableFuture<Result> prjMgrOrgsByPrjId(long id) {
        return super.http().get("mgr/prj/" + id + "/mgr-org", null);
    }

    public CompletableFuture<Result> prjRating(long id) {
        return super.http().get("mgr/prj/" + id + "/rating", null);
    }

    public CompletableFuture<Result> createPrjRating(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().post("mgr/prj/" + pid + "/rating", params);
    }

    public CompletableFuture<Result> queryAll(Map<String, Object> params) {
        return super.http().get("mgr/accounts", params);
    }

    public CompletableFuture<Result> previewLoanPrjBonus(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().get("mgr/prj/loan-projs/" + pid + "/preview-bonus", params);
    }

    public CompletableFuture<Result> updateBonusPeriod(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().post("mgr/prj/loan-projs/" + pid + "/bonus-period", params);
    }

    public CompletableFuture<Result> updateFinancier(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().post("mgr/prj/loan-projs/" + pid + "/related-financier", params);
    }

    public CompletableFuture<Result> updateLoanFinancier(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().post("mgr/prj/loan-projs/" + pid + "/financier", params);
    }

    public CompletableFuture<Result> delayFinancingDate(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().post("mgr/prj/loan-projs/" + pid + "/financing-period-delay", params);
    }

    public CompletableFuture<Result> queryPermissible(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().get("mgr/prj/" + pid + "/permissible-investor", params);
    }

    public CompletableFuture<Result> deletePermissible(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        long auId = takeLong(params, "au-id");
        return super.http().delete("mgr/prj/" + pid + "/permissible-investor/" + auId, params);
    }

    public CompletableFuture<Result> createPermission(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().put("mgr/prj/" + pid + "/permissible-investor", params);
    }

    //查询还款文件上传记录
    public CompletableFuture<Result> queryRepayHistory() {
        return super.http().get("mgr/prj/ts-repay-history", null);
    }

    public CompletableFuture<Result> deleteRepayHistory(Map<String, Object> params) {
        long trhId = takeLong(params, "trh-id");
        long batch = takeLong(params, "batch");
        return super.http().delete("mgr/prj/ts-repay-history/" + trhId + "/" + batch, null);
    }

    public CompletableFuture<Result> queryLoanHistory() {
        return super.http().get("mgr/prj/ts-loan-history", null);
    }

    public CompletableFuture<Result> deleteLoanHistory(Map<String, Object> params) {
        long tlhId = takeLong(params, "tlh-id");
        long batch = takeLong(params, "batch");
        return super.http().delete("mgr/prj/ts-loan-history/" + tlhId + "/" + batch, null);
    }

    //项目可见状态
    public CompletableFuture<Result> updatePrjVisible(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        return super.http().post("mgr/prj/" + pid + "/visibility", params);
    }

    public CompletableFuture<Result> goTop(long pid) {
        return super.http().post("mgr/prj/" + pid + "/top-time", null);
    }

    public CompletableFuture<Result> revokeTop(long pid) {
        return super.http().post("mgr/prj/" + pid + "/revoke/top-time", null);
    }

    //文件
    public CompletableFuture<Result> uploadBill(Map<String, Object> params) {
        long objectId = takeLong(params, "object-id");
        return super.http().post("mgr/files/" + objectId + "/protocol", params);
    }

    public CompletableFuture<Result> protocolList(Map<String, Object> params) {
        long objectId = takeLong(params, "object-id");
        return super.http().get("mgr/files/" + objectId + "/protocol", params);
    }

    public CompletableFuture<Result> delProtocol(Map<String, Object> params) {
        long objectId = takeLong(params, "object-id");
        return super.http().delete("mgr/files/" + objectId + "/protocol", params);
    }

    public CompletableFuture<Result> downloadProtocol(Map<String, Object> params) {
        return super.http().get("mgr/files/download-protocol", params);
    }

    //项目签章
    public CompletableFuture<Result> signatureList(long pid) {
        return super.http().get("protocol/" + pid + "/files", null);
    }

    public CompletableFuture<Result> abandon(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        int category = takeInt(params, "category");
        return super.http().delete("protocol/" + pid + "/abandon/" + category, params);
    }

    public CompletableFuture<Result> createAndUpload(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        long codeNo = takeLong(params, "code-no");
        return super.http().post("protocol/" + pid + "/create/" + codeNo, params);
    }

    public CompletableFuture<Result> sign(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        long category = takeLong(params, "category");
        return super.http().post("protocol/" + pid + "/sign/" + category, params);
    }

    public CompletableFuture<Result> checkSign(long pid) {
        return super.http().post("protocol/" + pid + "/check", null);
    }

    public CompletableFuture<Result> borSignInfo(long pid) {
        return super.http().get("protocol/" + pid + "/sign-links", null);
    }

    public CompletableFuture<Result> sync(Map<String, Object> params) {
        long pid = takeLong(params, "p-id");
        String fileName = takeString(params, "file-name");
        return super.http().post("protocol/" + pid + "/files/" + fileName + "/sync", params);
    }

}
