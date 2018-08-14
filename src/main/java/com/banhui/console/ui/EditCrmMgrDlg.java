package com.banhui.console.ui;


import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditCrmMgrDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditCrmMgrDlg.class);

    private volatile String uname;
    private Long id;

    public EditCrmMgrDlg(
            String uname
    ) {
        this.uname = uname;

        setTitle(getTitle() + uname);
        controller().setBoolean("recursive",true);

        new CrmProxy().getCrmMgrRelation(uname)
                      .thenApplyAsync(Result::map)
                      .whenCompleteAsync(this::queryCallback, UPDATE_UI);
    }

    private void queryCallback(
            Map<String, Object> map,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().setText("position", stringValue(map, "position"));
            controller().setText("department", stringValue(map, "department"));
            controller().setText("recursive", stringValue(map, "recursive"));
        }
    }


    @Override
    public void done(int result) {
        if (result == OK) {
            Map<String, Object> params = new HashMap<>();

            params.put("u-name", uname);
            params.put("position", controller().getText("position"));
            params.put("department", controller().getText("department"));
            params.put("recursive", controller().getBoolean("recursive"));

            new CrmProxy().updateCrmMgrRelation(params)
                          .thenApplyAsync(Result::longValue)
                          .whenCompleteAsync(this::callback, UPDATE_UI);
        } else {
            super.done(CANCEL);
        }
    }

    private void callback(
            Long result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.id = result;
            super.done(OK);
        }
    }

    public final String getUname() {
        return this.uname;
    }

    public final long getId() {
        return this.id;
    }
}
