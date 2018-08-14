package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.DialogUtils.prompt;

public class CrmCreateAssignDlg
        extends DialogPane {

    public CrmCreateAssignDlg() {
        controller().setText("department", null);
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();
            params.put("real-name", controller().getText("real-name"));
            params.put("mobile", controller().getText("mobile"));
            params.put("u-name", controller().getText("u-name"));
            params.put("department", controller().getText("department"));

            new CrmProxy().createAssign(params)
                          .thenApplyAsync(Result::longValue)
                          .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Long num,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
            controller().enable("ok");
        } else {
            prompt(null, controller().getMessage("create-success"));
            super.done(OK);
        }
    }
}
