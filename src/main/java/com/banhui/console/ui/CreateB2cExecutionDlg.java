package com.banhui.console.ui;


import com.banhui.console.rpc.B2cTransProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CreateB2cExecutionDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(CreateB2cExecutionDlg.class);

    private volatile long id;

    private Map<String, Object> row;

    public CreateB2cExecutionDlg(
            long id
    ) {
        this.id = id;

        setTitle(getTitle() + id);
    }


    @Override
    public void done(int result) {
        if (result == OK) {
            Map<String, Object> params = new HashMap<>();

            params.put("tbd-id", id);
            params.put("login-name", controller().getText("loginName"));
            params.put("real-name", controller().getText("name"));
            params.put("amt", controller().getDecimal("amt"));

            new B2cTransProxy().createMerXfer(params)
                               .thenApplyAsync(Result::map)
                               .whenCompleteAsync(this::createCallback, UPDATE_UI);
        } else {
            super.done(CANCEL);
        }
    }

    private void createCallback(
            Map<String, Object> row,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.row = row;
            super.done(OK);
        }
    }

    public final long getId() {
        return this.id;
    }

    public Map<String, Object> getRow() {
        return row;
    }
}
