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

public class CreatePrjBonusDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(CreatePrjBonusDlg.class);

    private volatile long pId;
    private volatile BigDecimal amt;
    private volatile Date dueTime;
    private volatile long tranType;
    private volatile long tranNo;

    private Map<String, Object> row;

    public CreatePrjBonusDlg(
            long pId,BigDecimal amt,Date dueTime,long tranType,long tranNo
    ) {
        this.pId = pId;
        this.amt = amt;
        this.dueTime = dueTime;
        this.tranType = tranType;
        this.tranNo = tranNo;

        setTitle(getTitle() + pId);

        if(tranNo != 0 && dueTime != null){

            controller().readOnly("tran-no",true);
            controller().readOnly("tran-type",true);
            controller().setDecimal("amt",amt);
            controller().setDate("due-time",dueTime);
            controller().setNumber("tran-no",tranNo);
            controller().setNumber("tran-type",tranType);
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
            params.put("tran-no",controller().getNumber("tran-no"));
            params.put("tran-type",controller().getNumber("tran-type"));
            params.put("amt",controller().getDecimal("amt"));
            params.put("due-time",controller().getDate("due-time"));
            (tranNo > 0 ? new ProjectRepayProxy().editPrjBonus(params):new ProjectRepayProxy().createPrjBonus(params))
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
