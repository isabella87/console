package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CrmResetManagerDlg
        extends DialogPane {
    private volatile long id;

    public CrmResetManagerDlg(
            long id,
            String name
    ) {
        this.id = id;
        if (id != 0) {
            setTitle(getTitle() + id);
        }
        controller().disable("do-reset");
        controller().connect("list", "change", this::listChanged);
        controller().connect("do-reset", this::resetManager);
        controller().connect("browse-history", this::browseHistory);
        controller().setText("name", name);

        JLabel perAmtLable = controller().get(JLabel.class, "name");
        perAmtLable.setForeground(Color.blue);

        final Map<String, Object> params = new HashMap<>();
        params.put("if-self", false);
        new CrmProxy().getAllMgrRelations(params)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle);
    }

    private void resetManager(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        String uName = tableModel.getStringByName(selectedRow, "uName");
        if (uName.equals("我自己")) {
            uName = "+";
        }
        final Map<String, Object> params = new HashMap<>();
        params.put("au-id", id);
        params.put("u-name", uName);
        new CrmProxy().bindRegUserWithMgr(params)
                      .thenApplyAsync(Result::longValue)
                      .whenCompleteAsync(this::updateCallBack, UPDATE_UI);

    }

    private void updateCallBack(
            Long num,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            super.done(OK);
        }
    }


    private void browseHistory(
            ActionEvent actionEvent
    ) {
        CrmHistoryMgrsDlg dlg = new CrmHistoryMgrsDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("uName", "我自己");
        c.add(params);
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("do-reset");
        } else if (selectedRows.length > 1) {
            controller().disable("do-reset");
        } else {
            controller().disable("do-reset");
        }
    }
}
