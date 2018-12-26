package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseAssignRegClientFrame
        extends InternalFramePane {

    public BrowseAssignRegClientFrame() {
        controller().readOnly("u-name", true);
        controller().setText("u-name", "全部");
        controller().disable("acc-info");
        controller().disable("reset-manager");

        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("list", "change", this::listChanged);
        controller().connect("search", this::search);
        controller().connect("choose-manager", this::chooseManager);
        controller().connect("acc-info", this::browseAccInfo);
        controller().connect("reset-manager", this::resetManager);
        controller().connect("create-assign", this::createAssign);
        controller().connect("batch-assign", this::batchAssign);
        controller().connect("fast-assign", this::fastAssign);

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        MainFrame.setTableTitleAndTableModel(getTitle(),tableModel);
    }

    private void chooseManager(
            ActionEvent actionEvent
    ) {
        CrmChooseManagerDlg dlg = new CrmChooseManagerDlg(1);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            String uName = dlg.getUName();
            if (!uName.isEmpty()) {
                controller().setText("u-name", uName);
            }
        }
    }

    private void resetManager(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        long id = tableModel.getNumberByName(selectedRow, "auId");
        String name = tableModel.getStringByName(selectedRow, "realName");
        CrmResetManagerDlg dlg = new CrmResetManagerDlg(id, name);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            controller().call("search");
        }
    }

    private void createAssign(
            ActionEvent actionEvent
    ) {
        CrmCreateAssignDlg dlg = new CrmCreateAssignDlg();
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            controller().call("search");
        }
    }

    private void batchAssign(
            ActionEvent actionEvent
    ) {
        CrmBatchAssignDlg dlg = new CrmBatchAssignDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
        controller().call("search");
    }

    private void fastAssign(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            long id = tableModel.getNumberByName(i, "auId");
            ids.add(id);
        }
        CrmFastAssignDlg dlg = new CrmFastAssignDlg(ids);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void browseAccInfo(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        long id = tableModel.getNumberByName(selectedRow, "auId");
        EditPerAccountInfoDlg dlg = new EditPerAccountInfoDlg(id, 0);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();

        String department = controller().getText("department");
        Long jxStatus = controller().getNumber("jx-status");
        String searchKey = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));
        String uName = controller().getText("u-name");
        switch (uName) {
            case "我自己":
                uName = "+";
                break;
            case "全部":
                uName = "*";
                break;
            case "无":
                uName = "";
                break;
        }
        params.put("u-name", uName);
        params.put("department", department);
        params.put("start-date1", startDate);
        params.put("end-date1", endDate);
        if (jxStatus != Integer.MAX_VALUE) {
            params.put("jx-status", jxStatus);
        }
        if (!searchKey.isEmpty()) {
            params.put("search-key", searchKey);
        }
        controller().disable("search");
        new CrmProxy().queryRegUsers(params)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle)
                      .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }

    private void accelerateDate(
            Object event
    ) {
        final int years = controller().getInteger("accelerate-date");
        if (years >= 0) {
            DateRange dateRange = latestSomeYears(new Date(), years);
            if (dateRange != null) {
                controller().setDate("start-date", dateRange.getStart());
                controller().setDate("end-date", dateRange.getEnd());
            }
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("acc-info");
            controller().enable("reset-manager");
        } else if (selectedRows.length > 1) {
            controller().disable("acc-info");
            controller().disable("reset-manager");
        } else {
            controller().disable("acc-info");
            controller().disable("reset-manager");
        }
    }
}
