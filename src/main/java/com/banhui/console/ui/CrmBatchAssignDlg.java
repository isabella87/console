package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CrmBatchAssignDlg
        extends DialogPane {
    private volatile int rowIndex;

    public CrmBatchAssignDlg() {
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
                    params.put("real-name", tableModel.getStringByName(i, "realName"));
                    params.put("mobile", tableModel.getStringByName(i, "mobile"));
                    params.put("u-name", tableModel.getStringByName(i, "uName"));
                    params.put("department", "");
                    retList.add(params);
                }
                return retList;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在执行:(姓名- " + params.get("real-name") + ",手机号-" + params.get("mobile") + ",客户经理-" + params.get("u-name");
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            ) {
                setRowIndex(i);
                new CrmProxy().createAssign(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::saveCallback, UPDATE_UI).join();
            }

            private void saveCallback(
                    Long num,
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
                tableModel.setValueAt(ctx, row, 3);
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
        if (lists != null && !lists.isEmpty()) {
            tableModel.setAllRows(lists);
            String batchInSuccess = controller().formatMessage("load-success", lists.size());
            prompt(this.getOwner(), batchInSuccess);
        } else if (lists.isEmpty()) {
        } else {
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
