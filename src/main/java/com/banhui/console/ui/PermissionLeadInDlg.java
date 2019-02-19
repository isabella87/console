package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import static org.xx.armory.swing.DialogUtils.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class PermissionLeadInDlg
        extends DialogPane {
    private volatile long id;
    private Map<String, Object> row;

    public PermissionLeadInDlg(
            long id
    ) {
        this.id = id;
    }


    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();
            if (this.id != 0) {
                params.put("p-id", id);
            }
            params.put("login-name", controller().getText("login-name"));
            params.put("mobile", controller().getText("mobile"));
            new ProjectProxy().createPermission(params)
                              .thenApplyAsync(Result::map)
                              .whenCompleteAsync(this::saveCallback, UPDATE_UI);

        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Map<String, Object> row,
            Throwable t
    ) {
        if (t != null) {
            controller().enable("ok");
//            fail(this.getOwner(), t.getCause().getMessage());
            ErrorHandler.handle(t);
        } else {
            this.row = row;
            super.done(OK);
        }
    }

    public Map<String, Object> getResultRow() {
        return this.row;
    }
}
