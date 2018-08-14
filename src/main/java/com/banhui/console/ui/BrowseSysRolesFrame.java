package com.banhui.console.ui;

import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.SysProxy;
import org.xx.armory.swing.UIUtils;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

/**
 * 浏览后台帐户的窗口。
 */
public class BrowseSysRolesFrame
        extends InternalFramePane {
    public BrowseSysRolesFrame() {
        controller().connect("search", this::search);
        controller().connect("create", this::create);
        controller().connect("edit", this::edit);
        controller().connect("perms", this::perm);
        controller().connect("delete", this::delete);
        controller().connect("list", "change", this::listChanged);

        controller().disable("edit");
        controller().disable("perms");
        controller().disable("delete");

        controller().setBoolean("enabled", true);

    }

    private void perm(ActionEvent actionEvent) {
    }

    private void search(
            ActionEvent event
    ) {
        final Map<String, Object> params = new HashMap<>();

        final Boolean enabled = controller().getBoolean("enabled");
        final String key = controller().getText("search-key");

        params.put("enabled", enabled);
        params.put("keyword", key);

        controller().disable("search");

        new SysProxy().findAllRoles(params)
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
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(c);
        }

        controller().enable("search");
    }

    private void create(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;

        final EditSysRoleDlg dlg = new EditSysRoleDlg("");
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            final Map<String, Object> row = dlg.getResultObj();

            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
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
        final String name = tableModel.getStringByName(selectedRow, "name");

        final EditSysRoleDlg dlg = new EditSysRoleDlg(name);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            final Map<String, Object> row = dlg.getResultObj();

            if (row != null && !row.isEmpty()) {
                tableModel.setRow(selectedRow, row);
            }
        }
    }

    private void delete(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        final String name = tableModel.getStringByName(selectedRow, "name");

        final String message = controller().formatMessage("confirm-delete", name);
        final String expectedValue = controller().getMessage("confirm-delete-expected");
        if (confirm(null, message, expectedValue)) {
            controller().disable("delete");

            new SysProxy().deleteRole(name)
                          .thenApplyAsync(Result::map)
                          .whenCompleteAsync(this::deleteCallback, UPDATE_UI);
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            // 选中了行，并且仅选中一行。
            controller().enable("edit");
            controller().enable("perms");
            controller().enable("delete");
        } else if (selectedRows.length > 1) {
            controller().disable("edit");
            controller().disable("perms");
            controller().disable("delete");
        }
    }

    private void deleteCallback(
            Map<String, Object> deletedRow,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();

            if (deletedRow != null && !deletedRow.isEmpty()) {
                final Object name = deletedRow.get("name");
                tableModel.removeFirstRow(row -> UIUtils.equalsWidely(row.get("name"), name));
            }
        }

        controller().enable("delete");
    }
}
