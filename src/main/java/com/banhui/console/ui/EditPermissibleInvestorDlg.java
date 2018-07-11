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

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditPermissibleInvestorDlg
        extends DialogPane {

    private volatile long id;

    public EditPermissibleInvestorDlg(
            long id
    ) {
        this.id = id;
        if (this.id != 0) {
            setTitle(getTitle() + id);
        }
        controller().disable("delete");

        controller().connect("leadIn", this::leadIn);
        controller().connect("batchIn", this::batchIn);
        controller().connect("search", this::search);
        controller().connect("delete", this::delete);
        controller().connect("list", "change", this::listChanged);

        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", id);
        new ProjectProxy().queryPermissible(params)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void batchIn(
            ActionEvent actionEvent
    ) {
        final PermissionBatchInDlg dlg = new PermissionBatchInDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
        controller().call("search");
    }

    private void leadIn(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;

        final PermissionLeadInDlg dlg = new PermissionLeadInDlg(id);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
    }

    private void delete(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", id);
        String confirmDeleteText = controller().formatMessage("confirm-delete-text");
        if (confirm(null, confirmDeleteText)) {
            controller().disable("delete");
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final long auId = tableModel.getNumberByName(table.getSelectedRow(), "auId");
            params.put("au-id", auId);
            new ProjectProxy().deletePermissible(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            Long auId,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(auId.toString(), row.get("auId")));
        }
        controller().disable("delete");
    }


    private void search(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", id);
        params.put("key", controller().getText("key"));
        new ProjectProxy().queryPermissible(params)
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

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("delete");
        } else if (selectedRows.length > 1) {
            controller().disable("delete");
        } else {
            controller().disable("delete");
        }
    }
}
