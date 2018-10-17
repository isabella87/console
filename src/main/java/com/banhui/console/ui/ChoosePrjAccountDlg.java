package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class ChoosePrjAccountDlg extends DialogPane {

    private volatile String realName;
    private volatile long auId;
    private volatile String userType;
    private volatile long allowRole;

    public ChoosePrjAccountDlg(
            long allowRole
    ) {
        this.allowRole = allowRole;
        if (allowRole == 2) {
            setTitle(controller().getMessage("borPer"));
        } else if (allowRole == 3) {
            setTitle(controller().getMessage("bondsMan"));
        }
        controller().connect("list", "change", this::listChanged);
        controller().connect("search", this::search);
        controller().disable("ok");

        final Map<String, Object> params = new HashMap<>();
        params.put("allow-role", allowRole);
        new ProjectProxy().queryAll(params)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);

    }

    private void search(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("allow-role", allowRole);
        final long userType = controller().getNumber("user-type");
        if (userType != Integer.MAX_VALUE) {
            params.put("user-type", userType);
        }
        params.put("key", controller().getText("key"));

        new ProjectProxy().queryAll(params)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            JTable table1 = controller().get(JTable.class, "list");
            TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
            tableModel1.setAllRows(c);
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            JTable table = controller().get(JTable.class, "list");
            TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final int selectedRow = table.getSelectedRow();

            final String realName = tableModel.getStringByName(selectedRow, "realName");
            final long auId = Long.valueOf(tableModel.getStringByName(selectedRow, "auId"));
            final String userType = tableModel.getStringByName(selectedRow, "userType");
            this.setRealName(realName);
            this.setAuId(auId);
            this.setUserType(userType);
            super.done(OK);
        } else {
            super.done(result);
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("ok");
        } else if (selectedRows.length > 1) {
            controller().disable("ok");
        } else {
            controller().disable("ok");
        }
    }

    public long getAuId() {
        return auId;
    }

    public void setAuId(long auId) {
        this.auId = auId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


}
