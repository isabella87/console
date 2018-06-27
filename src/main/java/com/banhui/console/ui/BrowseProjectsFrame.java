package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseProjectsFrame
        extends InternalFramePane {
    /**
     * {@inheritDoc}
     */
    public BrowseProjectsFrame() {
        controller().connect("search", this::search);
        controller().connect("audit", this::audit);
        controller().connect("repay", this::repay);
        controller().connect("view", this::view);
        controller().connect("edit", this::edit);
        controller().connect("create", this::create);
        controller().connect("delete", this::delete);
        controller().connect("repay-history", this::repayHistory);
        controller().connect("list", "change", this::listChanged);

        controller().disable("audit");
        controller().disable("view");
        controller().disable("edit");
        controller().disable("repay");
        controller().disable("delete");

        controller().setNumber("status", 40L);
    }

    private void repayHistory(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        final BrowseRepayHistoryDlg dlg = new BrowseRepayHistoryDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void repay(ActionEvent actionEvent) {

        assertUIThread();

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();

        final int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }
        final long pId = tableModel.getNumberByName(selectedRow, "pId");
        final long status = tableModel.getNumberByName(selectedRow, "status");

        EditPrjRepayDlg dlg = new EditPrjRepayDlg(pId, status);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final int dateType = controller().getInteger("date-type");
        final int status = Integer.valueOf(controller().getText("status"));
        final int keyType = controller().getInteger("key-type");
        final String key = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
        switch (dateType) {
            case 1:
                params.put("start-time", startDate);
                params.put("end-time", endDate);
                break;
            case 2:
                params.put("on-line-start-time", startDate);
                params.put("on-line-end-time", endDate);
                break;
            case 3:
                params.put("loan-start-time", startDate);
                params.put("loan-end-time", endDate);
                break;
            case 4:
                params.put("capital-repay-start-time", startDate);
                params.put("capital-repay-end-time", endDate);
                break;
            case 5:
                params.put("clear-start-time", startDate);
                params.put("clear-end-time", endDate);
                break;
        }

        if (status != Integer.MAX_VALUE) {
            params.put("status", status);
        }

        switch (keyType) {
            case 1:
                params.put("item-no-key", key);
                break;
            case 2:
                params.put("item-name-key", key);
                break;
            case 3:
                params.put("financier-key", key);
                break;
            case 4:
                params.put("creator-key", key);
                break;
        }

        controller().disable("search");

        new ProjectProxy().allProjects(params)
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

    private void view(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        final long pId = tableModel.getNumberByName(table.getSelectedRow(), "pId");
        final EditProjectsDlg dlg = new EditProjectsDlg(pId, 0);
        final long type = tableModel.getNumberByName(selectedRow, "status");
        if (type == 40) {
            dlg.controller().enable("delay");
        }
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            tableModel.setRow(selectedRow, row);
        }
    }

    private void create(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;

        final CreateProjectFrame dlg = new CreateProjectFrame();
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
        long pId = dlg.getId();
        if (pId != 0) {
            final EditProjectsDlg dlg2 = new EditProjectsDlg(pId, 1);
            dlg2.setFixedSize(false);
            showModel(null, dlg2);
        }
    }

    private void edit(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        final long pId = tableModel.getNumberByName(table.getSelectedRow(), "pId");
        final EditProjectsDlg dlg = new EditProjectsDlg(pId, 1);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            tableModel.setRow(selectedRow, row);
        }
    }

    private void delete(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long pId = tableModel.getNumberByName(table.getSelectedRow(), "pId");

        String confirmDeleteText = controller().formatMessage("confirm-delete-text", pId);
        if (confirm(null, confirmDeleteText)) {
            controller().disable("delete");

            new ProjectProxy().deletePrjLoan(pId)
                              .thenApplyAsync(Result::map)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            Map<String, Object> deletedRow,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(deletedRow.get("pId"), row.get("pId")));
        }
        controller().enable("delete");
    }

    private void audit(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long id = tableModel.getNumberByName(table.getSelectedRow(), "pId");
        final long status = tableModel.getNumberByName(table.getSelectedRow(), "status");
        final AuditProjectsDlg dlg = new AuditProjectsDlg(id, status);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }


    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        if (table.getSelectedRow() > -1) {
            final long status = tableModel.getNumberByName(table.getSelectedRow(), "status");
            if (selectedRows.length == 1) {
                // 选中了行，并且仅选中一行。
                if (status == 0) {
                    controller().enable("delete");
                    controller().show("edit");
                    controller().enable("edit");
                    controller().hide("view");
                    controller().enable("audit");

                } else {
                    controller().disable("delete");
                    controller().hide("edit");
                    controller().show("view");
                    controller().enable("view");
                    controller().enable("audit");
                    if (status >= 90) {
                        controller().enable("repay");
                    } else {
                        controller().disable("repay");
                    }
                }
            } else if (selectedRows.length > 1) {
                controller().disable("view");
                controller().disable("edit");
                controller().disable("audit");
                controller().disable("repay");
            }
        } else {
            controller().disable("view");
            controller().disable("edit");
            controller().disable("delete");
            controller().disable("audit");
            controller().disable("repay");
        }
    }
}
