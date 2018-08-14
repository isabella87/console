package com.banhui.console.ui;


import com.banhui.console.rpc.B2cTransProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class B2cExecutionDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(B2cExecutionDlg.class);

    private volatile long id;

    public B2cExecutionDlg(
            long id
    ) {
        this.id = id;

        setTitle(getTitle() + id);

        controller().connect("create", this::create);
        controller().connect("batchImport", this::batchImport);
        controller().connect("execute", this::execute);
        controller().connect("refresh", this::refresh);

        controller().call("refresh");

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
                    long done = tableModel.getNumberByName(row, "done");
                    if (done == 1) {
                        continue;
                    }
                    Map<String, Object> params = new HashMap<>();
                    params.put("tbdId", id);
                    params.put("id", tableModel.getValueByName(row, "jvpId"));
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
                                   .thenApplyAsync(Result::map)
                                   .whenCompleteAsync(this::executeCallback, UPDATE_UI).join();

            }

            private void executeCallback(
                    Map<String, Object> row,
                    Throwable t
            ) {
                if (t != null) {
//                    ErrorHandler.handle(t);
                    return;
                } else {
                    //TODO 更新行状态
                }
            }
        });
        showModel(null, dlg);
    }


    private void refresh(
            ActionEvent event
    ) {
        new B2cTransProxy().queryMerXfers(id)
                           .thenApplyAsync(Result::list)
                           .whenCompleteAsync(this::refreshCallback, UPDATE_UI);
    }

    private void refreshCallback(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(maps);
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
