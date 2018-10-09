package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.banhui.console.rpc.ResultUtils.longValue;

public class ProtocolSignProxy extends AbstractProxy{


    /**
     * 强制债权转让协议相关服务
     * @param params
     * @return
     */
    public CompletableFuture<Result> uploadCreditAssignFile(Map<String,Object> params){
        return super.http().post("/protocol/force-credit-upload",params);
    }

    public CompletableFuture<Result> doCreditAssignSign(Map<String,Object> params){
        return super.http().post("/protocol/force-credit-sign",params);
    }

    public CompletableFuture<Result> download(Map<String,Object> params){
        return super.http().get("/protocol/download",params);
    }

    public CompletableFuture<Result> getForceCreditAgreementFilesInfoByPId(Map<String,Object> params){
        long pId = longValue(params,"pId");
        return super.http().get("/protocol/"+pId+"/force-credit-prj",params);
    }

    public CompletableFuture<Result> getCreditAccountInfoByPId(Map<String,Object> params){
        long pId = longValue(params,"pId");
        return super.http().post("/protocol/"+pId+"/credit-account-sign-info",params);
    }

}
