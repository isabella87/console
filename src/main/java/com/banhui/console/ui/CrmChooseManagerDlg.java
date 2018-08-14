package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CrmChooseManagerDlg
        extends DialogPane {
    private volatile String uName;

    public String getuName() {
        return uName;
    }

    public CrmChooseManagerDlg() {
        controller().disable("ok");
        controller().connect("list", "change", this::listChanged);

        final Map<String, Object> params = new HashMap<>();
        params.put("if-self", false);
        new CrmProxy().getAllMgrRelations(params)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("uName", "我自己");
        final Map<String, Object> params1 = new HashMap<>();
        params1.put("uName", "全部");
        final Map<String, Object> params2 = new HashMap<>();
        params2.put("uName", "无");
        c.add(params);
        c.add(params1);
        c.add(params2);

        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final int selectedRow = table.getSelectedRow();
            this.uName = tableModel.getStringByName(selectedRow, "uName");
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
}
