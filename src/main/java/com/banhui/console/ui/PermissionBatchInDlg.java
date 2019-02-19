package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionBatchInDlg
        extends DialogPane {
    private volatile long id;
    private volatile int rowIndex;

    public PermissionBatchInDlg(
            long id
    ) {
        this.id = id;

        controller().disable("delete");

        controller().connect("load", this::load);
        controller().connect("delete", this::delete);
        controller().connect("begin", this::begin);
        controller().connect("list", "change", this::listChanged);
    }

    private void begin(
            ActionEvent actionEvent
    ) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

            @Override
            public String getTitle() {
                return controller().getMessage("batchIn-title");
            }

            @Override
            protected Collection<Map<String, Object>> load() {
                final JTable table = controller().get(JTable.class, "list");
                final TypedTableModel tableModel = (TypedTableModel) table.getModel();

                List<Map<String, Object>> retList = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("p-id", id);
                    params.put("login-name", tableModel.getStringByName(i, "loginName"));
                    params.put("mobile", tableModel.getStringByName(i, "mobile"));
                    retList.add(params);
                }
                return retList;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在执行:(登录名- " + params.get("login-name") + ",手机号-" + params.get("mobile");
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            ) {
                setRowIndex(i);
                new ProjectProxy().createPermission(params)
                                  .thenApplyAsync(Result::map)
                                  .whenCompleteAsync(this::saveCallback, UPDATE_UI).join();
            }

            private void saveCallback(
                    Map<String, Object> map,
                    Throwable t
            ) {
                final JTable table = controller().get(JTable.class, "list");
                final TypedTableModel tableModel = (TypedTableModel) table.getModel();
                String ctx;
                int row = getRowIndex();
                if (t != null) {
                    ctx = t.getCause().getMessage();
                } else {
                    ctx = controller().getMessage("success");
                }
                tableModel.setValueAt(ctx, row, 2);
            }
        });
        showModel(null, dlg);
    }


    private void delete(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        int modelSelectRow = table.convertRowIndexToModel(selectedRow);
        tableModel.removeRow(modelSelectRow);
    }

    private void load(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        List<Map<String, Object>> lists = new ExcelImportUtil(tableModel).readExcel();
        if (lists != null&&!lists.isEmpty()) {
            tableModel.setAllRows(lists);
            String batchInSuccess = controller().formatMessage("load-success", lists.size());
            prompt(this.getOwner(), batchInSuccess);
        } else if(lists.isEmpty()){

        }else {
            prompt(this.getOwner(), controller().getMessage("load-fail"));
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

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
