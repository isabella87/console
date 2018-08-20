package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseDailyPerfStatisticsFrame
        extends InternalFramePane {

    public BrowseDailyPerfStatisticsFrame() {
        controller().disable("name-list");
        controller().setDate("datepoint", new Date());

        controller().connect("refresh", this::refreshUser);
        controller().connect("list", "change", this::listChanged);
        controller().connect("choose-depart", this::chooseDepart);
        controller().connect("statistics", this::doStatistics);
        controller().call("refresh");
    }

    private void doStatistics(
            ActionEvent actionEvent
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "user-list").getModel();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String uName = tableModel.getStringByName(i, "uName") + ",";
            Boolean check = tableModel.getBooleanByName(i, "check");
            if (check != null && check) {
                sb.append(uName);
            }
        }

        final Map<String, Object> params = new HashMap<>();
        params.put("datepoint", controller().getDate("datepoint"));
        params.put("u-names", sb.toString());
        new CrmProxy().queryUserDaily(params)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle);

    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }

    private void chooseDepart(
            ActionEvent actionEvent
    ) {
        String department = controller().getText("department");
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "user-list").getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String depart = tableModel.getStringByName(i, "department");
            if (department.equals(depart)) {
                tableModel.setValueAt(true, i, 0);
            }
        }
    }

    private void refreshUser(
            ActionEvent actionEvent
    ) {

        final Map<String, Object> params = new HashMap<>();
        params.put("if-self", false);
        new CrmProxy().getAllMgrRelations(params)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::searchUserBack, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle);
    }

    private void searchUserBack(
            Collection<Map<String, Object>> c
    ) {
        final Map<String, Object> params = new HashMap<>();
        //params.put("uName", "我自己");
        c.add(params);
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "user-list").getModel();
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
