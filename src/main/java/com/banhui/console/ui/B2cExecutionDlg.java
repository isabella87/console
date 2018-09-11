package com.banhui.console.ui;


import com.banhui.console.rpc.B2cTransProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.DialogUtils;
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

import static com.banhui.console.rpc.ResultUtils.longValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class B2cExecutionDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(B2cExecutionDlg.class);

    private volatile long id;
    private Collection<Map<String, Object>> children;

    public B2cExecutionDlg(
            long id
    ) {
        this.id = id;

        setTitle(getTitle() + id);

        controller().connect("create", this::create);
        controller().connect("batchImport", this::batchImport);
        controller().connect("execute", this::execute);
        controller().connect("refresh", this::refresh);
        controller().connect("list", "change", this::listChange);

        controller().call("refresh");

    }

    private void listChange(Object o) {

        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final TypedTableModel detailTableModel = (TypedTableModel) controller().get(JTable.class, "detail").getModel();
        Collection<Map<String, Object>> child = new ArrayList<>();

        int selectedRow = table.getSelectedRow();
        if (table.getSelectedRow() > -1) {
            long tvId = tableModel.getNumberByName(selectedRow, "tvId");
            for (Map<String, Object> item : children) {
                if (Long.valueOf(item.get("tsId").toString()) == tvId) {
                    child.add(item);
                }
            }
        }
        detailTableModel.setAllRows(child);
    }

    private void batchImport(ActionEvent actionEvent) {

        BatchImportB2cDlg dlg = new BatchImportB2cDlg(id);
        dlg.setFixedSize(false);

        showModel(null, dlg);

        controller().call("refresh");
    }

    private void execute(ActionEvent actionEvent) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

            @Override
            public String getTitle() {
                return controller().getMessage("execute-title");
            }

            @Override
            protected Collection<Map<String, Object>> load() {

                List<Map<String, Object>> returnList = new ArrayList<>();

                JTable table = controller().get(JTable.class, "list");
                TypedTableModel tableModel = (TypedTableModel) table.getModel();

                int rowCount = tableModel.getRowCount();
                for (int row = 0; row < rowCount; row++) {
                    long status = tableModel.getNumberByName(row, "status");
                    if (status == 1) {
                        continue;
                    }

                    boolean flag = false;
                    long tvId = tableModel.getNumberByName(row, "tvId");
                    for (Map<String, Object> item : children) {
                        if (longValue(item, "tsId") == tvId) {
                            if (longValue(item, "done") == 0 || longValue(item, "done") == 2) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        continue;
                    }

                    Map<String, Object> params = new HashMap<>();
                    params.put("tbdId", id);
                    params.put("id", tableModel.getValueByName(row, "tvId"));
                    returnList.add(params);
                }

                return returnList;


            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在执行: " + params.get("id");
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            )
                    throws Exception {

                new B2cTransProxy().execute(params)
                                   .thenApplyAsync(Result::booleanValue)
                                   .whenCompleteAsync(this::executeCallback, UPDATE_UI).join();

            }

            private void executeCallback(
                    boolean flag,
                    Throwable t
            ) {
                if (t != null) {
                    ErrorHandler.handle(t);
                    return;
                }
            }
        });
        showModel(null, dlg);
        controller().call("refresh");
    }


    private void refresh(
            ActionEvent event
    ) {
        new B2cTransProxy().queryMerXfers(id)
                           .thenApplyAsync(Result::map)
                           .whenCompleteAsync(this::refreshCallback, UPDATE_UI);
    }

    private void refreshCallback(
            Map<String, Object> map,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) map.get("items");
            children = (Collection<Map<String, Object>>) map.get("children");
            tableModel.setAllRows(items);
        }
    }

    private void create(
            ActionEvent actionEvent
    ) {

        CreateB2cExecutionDlg dlg = new CreateB2cExecutionDlg(id);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            JTable table = controller().get(JTable.class, "list");
            TypedTableModel tableModel = (TypedTableModel) table.getModel();
            Map<String, Object> row = dlg.getRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(0, row);
            }
        }
    }

    public final long getId() {
        return this.id;
    }

}
