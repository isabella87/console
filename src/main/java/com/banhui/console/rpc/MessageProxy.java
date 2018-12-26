package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MessageProxy
        extends AbstractProxy {

    public CompletableFuture<Result> createMsg(Map<String,Object> params) {
        return super.http().put("mgr/accounts/msg", params);
    }

    public CompletableFuture<Result> queryMsgListByCond(Map<String,Object> params) {
        return super.http().get("mgr/accounts/msg", params);
    }

    public CompletableFuture<Result> queryMsgDetailByAmId(long id){
        return super.http().get("mgr/accounts/msg/"+id,null);
    }

    public CompletableFuture<Result> queryYmMsgs(Map<String,Object> params){
        return super.http().get("mgr/ym/msgs",params);
    }
    public CompletableFuture<Result> matchAccUserInfoInvest(Map<String,Object> params){
        return super.http().get("mgr/accounts/invest/persons/matches",params);
    }

    public CompletableFuture<Result> getYmMsgTotal(Map<String,Object> params){
        return super.http().get("mgr/ym/msgs-total",params);
    }
}
