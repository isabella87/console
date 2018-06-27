package com.banhui.console.ui;


import com.banhui.console.rpc.ProjectRepayProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CreatePrjRepayDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(CreatePrjRepayDlg.class);

    private volatile long pId;
    private volatile BigDecimal unpaidAmt;
    private volatile Date dueTime;
    private volatile long tranType;
    private volatile long tranNo;

    private Map<String, Object> row;

    public CreatePrjRepayDlg(
            long pId,BigDecimal unpaidAmt,Date dueTime,long tranType,long tranNo
    ) {
        this.pId = pId;
        this.unpaidAmt = unpaidAmt;
        this.dueTime = dueTime;
        this.tranType = tranType;
        this.tranNo = tranNo;

        setTitle(getTitle() + pId);


        controller().setDecimal("unpaid-amt",unpaidAmt);
        controller().setDate("pay-time",dueTime);
        controller().setDate("real-paid-time",dueTime);
        controller().setDecimal("amt",BigDecimal.ZERO);
        controller().setDecimal("sub-amt",unpaidAmt);
        controller().setText("remark",tranType ==0? controller().getMessage("interest"):controller().getMessage("capital"));

        controller().connect("is-on-time-repay","change",this::change);
        controller().connect("amt","change",this::paidAmtChange);

        controller().readOnly("unpaid-amt",true);
        controller().setBoolean("is-on-time-repay",true);
        controller().readOnly("pay-time",true);
        controller().readOnly("real-paid-time",true);
        controller().readOnly("sub-amt",true);
    }

    private void paidAmtChange(Object o) {
        controller().setDecimal("sub-amt",unpaidAmt.subtract(controller().getDecimal("amt")));

    }

    private void change(Object o) {
       boolean flag = controller().getBoolean("is-on-time-repay");
       if(flag){
           controller().setDate("real-paid-time",dueTime);
           controller().readOnly("real-paid-time",true);
       }else{
           controller().readOnly("real-paid-time",false);
       }
    }


    public final long getId() {
        return this.pId;
    }

    public Map<String, Object> getResultRow() {
        return this.row;
    }

    @Override
    public void done(int result){
        if (result == OK){

            controller().disable("ok");

            Map<String,Object> params = new HashMap<>();
            params.put("pId",pId);
            params.put("tran-no",tranNo);
            params.put("tran-type",tranType);
            params.put("pay-type",controller().getNumber("pay-type"));
            params.put("amt",controller().getDecimal("amt"));
            params.put("paid-time",controller().getDate("real-paid-time"));
            params.put("remark",controller().getText("remark"));
           new ProjectRepayProxy().createPrjBonusDetail( params)
                                  .thenApplyAsync(Result::map)
                                  .whenCompleteAsync(this::submitCallback,UPDATE_UI);
        }
    }

    private void submitCallback(
            Map<String,Object> stringObjectMap,
            Throwable throwable
    ) {
        if(throwable != null){
            ErrorHandler.handle(throwable);
        }else{
            this.row = stringObjectMap;
            super.done(OK);
        }
        controller().enable("ok");
    }
}
