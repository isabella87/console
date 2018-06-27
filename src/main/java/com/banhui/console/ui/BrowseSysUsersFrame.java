package com.banhui.console.ui;

import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.SysProxy;
import org.xx.armory.swing.UIUtils;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.ListItem;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.ComponentUtils.updateDropDown;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

/**
 * 浏览后台帐户的窗口。
 */
public class BrowseSysUsersFrame
        extends InternalFramePane {
    public BrowseSysUsersFrame() {
        controller().connect("search", this::search);
        controller().connect("create", this::create);
        controller().connect("edit", this::edit);
        controller().connect("reset-password", this::resetPassword2);
        controller().connect("delete", this::delete);
        controller().connect("list", "change", this::listChanged);

        controller().disable("edit");
        controller().disable("reset-password");
        controller().disable("delete");

        controller().setBoolean("enabled", true);

        new SysProxy().allRoles(null)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::updateRoles, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle);
    }

    @SuppressWarnings("unchecked")
    private void updateRoles(
            List<Map<String, Object>> data
    ) {
        assertUIThread();

        final JComboBox<ListItem> roleName = controller().get(JComboBox.class, "role-name");
        updateDropDown(roleName, data.stream()
                                     .map(m -> new ListItem(m.get("title").toString(), m.get("name")))
                                     .toArray(ListItem[]::new));
    }

    private void search(
            ActionEvent event
    ) {
        final Map<String, Object> params = new HashMap<>();

        final String roleName = controller().getText("role-name");
        final Boolean enabled = controller().getBoolean("enabled");
        final String key = controller().getText("search-key");

        params.put("role-name", roleName);
        params.put("enabled", enabled);
        params.put("keyword", key);

        controller().disable("search");

        new SysProxy().allUsers(params)
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

        final EditSysUserDlg dlg = new EditSysUserDlg("");
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
        final String userName = tableModel.getStringByName(selectedRow, "userName");

        final EditSysUserDlg dlg = new EditSysUserDlg(userName);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            final Map<String, Object> row = dlg.getResultObj();

            if (row != null && !row.isEmpty()) {
                tableModel.setRow(selectedRow, row);
            }
        }
    }

    private void resetPassword(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        final String userName = tableModel.getStringByName(selectedRow, "userName");

        final String message = controller().formatMessage("confirm-reset-password", userName);
        if (confirm(null, message)) {
            controller().disable("reset-password");

            new SysProxy().resetPassword(userName)
                          .thenAcceptAsync(Result::none)
                          .whenCompleteAsync(this::resetPasswordCallback, UPDATE_UI);
        }
    }

    private void resetPassword2(
            ActionEvent event
    ) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<String>() {
            @Override
            public String getTitle() {
                return controller().getMessage("reset-password-execution");
            }

            @Override
            protected Collection<String> load() {
                final JTable table = controller().get(JTable.class, "list");
                final TypedTableModel tableModel = (TypedTableModel) table.getModel();
                final int[] selectedRows = table.getSelectedRows();
                return stream(selectedRows)
                        .mapToObj(row -> tableModel.getStringByName(row, "userName"))
                        .collect(Collectors.toList());
            }

            @Override
            protected String getCurrent(
                    int i,
                    String s
            ) {
                return "正在执行: " + s;
            }

            @Override
            protected void execute(
                    int i,
                    String id
            ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    return;
                }

                System.out.println("running: " + id);
            }
        });

        final int result = showModel(null, dlg);
        System.out.println("##" + result);
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
        final String userName = tableModel.getStringByName(selectedRow, "userName");

        final String message = controller().formatMessage("confirm-delete", userName);
        final String expectedValue = controller().getMessage("confirm-delete-expected");
        if (confirm(null, message, expectedValue)) {
            controller().disable("delete");

            new SysProxy().delete(userName)
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
            controller().enable("reset-password");
            controller().enable("delete");
        } else if (selectedRows.length > 1) {
            controller().disable("edit");
            controller().enable("reset-password");
            controller().disable("delete");
        }
    }

    private void resetPasswordCallback(
            Void dummy,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        }

        controller().enable("reset-password");
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
                final Object userName = deletedRow.get("userName");
                tableModel.removeFirstRow(row -> UIUtils.equalsWidely(row.get("userName"), userName));
            }
        }

        controller().enable("delete");
    }
}
