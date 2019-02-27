package com.banhui.console.ui;


import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.DialogUtils;
import org.xx.armory.swing.components.DialogPane;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class SetPrjRelationTimeDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(SetPrjRelationTimeDlg.class);

    private volatile long pId;

    private volatile boolean flag;//true 为逾期，否则为展期

    public SetPrjRelationTimeDlg(
            long pId,
            boolean flag
    ) {
        this.pId = pId;
        this.flag = flag;

        if (flag) {
            setTitle("逾期" + getTitle());
        } else {
            setTitle("展期" + getTitle());
        }
        controller().setDate("relation-date",new Date());

        new ProjectProxy().queryPrjLoanById(pId).thenApplyAsync(Result::map)
                          .whenCompleteAsync(this::searchPrjCallback,UPDATE_UI)
                          .whenCompleteAsync((dummy_, t_) -> ErrorHandler.handle(t_));
    }

    private void searchPrjCallback(
            Map<String, Object> rets,
            Throwable t
    ) {
        if(t!=null){
            ErrorHandler.handle(t);
        }else {
            Date timeOutDate = dateValue(rets,"timeOutDate");
            Date extensionDate = dateValue(rets,"extensionDate");
            if(flag && timeOutDate!=null){
                controller().disable("ok");
                controller().setDate("relation-date",timeOutDate);
            }else if(!flag &&extensionDate!=null){
                controller().disable("ok");
                controller().setDate("relation-date",extensionDate);
            }
        }
    }


    @Override
    public void done(int result) {
        if (result == OK) {
            Date flagDate = controller().getDate("relation-date");
            Map<String, Object> params = new HashMap<>();
            params.put("p-id", pId);
            params.put("relation-date", flagDate);
            if (flag) {
                new ProjectProxy().setPrjTimeOut(params).thenApplyAsync(Result::booleanValue).whenCompleteAsync(this::callback,UPDATE_UI);
            } else {
                new ProjectProxy().setPrjExtensionTime(params).thenApplyAsync(Result::booleanValue).whenCompleteAsync(this::callback,UPDATE_UI);
            }
            super.done(OK);

        } else {
            super.done(CANCEL);
        }
    }

    private void callback(
            Boolean ret,
            Throwable t
    ) {
        if(t != null){
            ErrorHandler.handle(t);
        }else{
            DialogUtils.prompt(null,"设置成功！");
        }
    }
}
