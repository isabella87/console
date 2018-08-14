package com.banhui.console.rpc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class B2cTransProxy
        extends AbstractProxy {

    public CompletableFuture<Result> createB2cDetail(Map<String,Object> params){
        return super.http().put("mgr/trans/b2c-details",params);
    }

    public CompletableFuture<Result> queryB2cDetails(Map<String,Object> params){
        return super.http().get("mgr/trans/b2c-details",params);
    }

    public CompletableFuture<Result> deleteB2cDetail(long tbdId){
        return super.http().delete("mgr/trans/b2c-details/"+ tbdId,null);
    }

   public CompletableFuture<Result> createMerXfer(Map<String,Object> params){
        long tbdId = takeLong(params,"tbd-id");
       return super.http().put("mgr/trans/b2c-details/"+ tbdId + "/mer-xfer",params);
   }

    public CompletableFuture<Result> queryMerXfers(long tbdId){
        return super.http().get("mgr/trans/b2c-details/"+ tbdId + "/mer-xfer",null);
    }

    public CompletableFuture<Result> execute(Map<String,Object> params){
        long tbdId = takeLong(params,"tbdId");
        long id = takeLong(params,"id");
        return super.http().post("mgr/trans/b2c-details/"+ tbdId +"/mer-xfer/" + id +"/execution",null);
    }

}
