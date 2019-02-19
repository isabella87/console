package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class DelayFinancingDateDlg
        extends DialogPane {
    private volatile long id;
    private volatile Date datepoint;

    public Date getDatepoint() {
        return datepoint;
    }

    public DelayFinancingDateDlg(
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
            params.put("datepoint", controller().getDate("datepoint"));
            new ProjectProxy().delayFinancingDate(params)
                              .thenApplyAsync(Result::booleanValue)
                              .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (flag) {
                datepoint = controller().getDate("datepoint");
            }
            super.done(OK);
        }
    }
}
