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
import java.util.Objects;

import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseRepayHistoryDlg
        extends DialogPane {
    public BrowseRepayHistoryDlg() {

        controller().connect("refresh", this::refresh);
        controller().connect("delete", this::delete);
        controller().connect("list", "change", this::listChanged);

        controller().call("refresh");
    }

    private void delete(
            ActionEvent actionEvent

    ) {
        controller().disable("delete");
        String confirmDeleteText = controller().formatMessage("delete-confirm");
        String confirmText = controller().formatMessage("confirm-text");
        if (confirm(this.getOwner(), confirmDeleteText, confirmText)) {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final long trhId = tableModel.getNumberByName(table.getSelectedRow(), "trhId");
            final long batch = tableModel.getNumberByName(table.getSelectedRow(), "batch");
            final Map<String, Object> params = new HashMap<>();
            params.put("trh-id", trhId);
            params.put("batch", batch);
            new ProjectProxy().deleteRepayHistory(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            Long id,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
            prompt(null, controller().getMessage("delete-fail"));
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(id.toString(), row.get("trhId")));
            prompt(null, controller().getMessage("delete-success"));
        }
    }

    private void refresh(
            ActionEvent actionEvent
    ) {
        new ProjectProxy().queryRepayHistory()
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);
        controller().disable("delete");
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

    private void listChanged(
            Object o
    ) {
        JTable table = controller().get(JTable.class, "list");
        int[] selectedRows = table.getSelectedRows();
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        if (table.getSelectedRow() > -1) {
            long status = tableModel.getNumberByName(table.getSelectedRow(), "status");
            if (selectedRows.length == 1 && status == -1) {
                controller().enable("delete");
            } else if (selectedRows.length > 1) {
                controller().enable("delete");
            } else {
                controller().disable("delete");
            }
        }
    }
}
