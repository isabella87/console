package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.ProjectProxy;
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
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class BrowseCheckTenderDlg
        extends DialogPane {
    private volatile long id;

    public BrowseCheckTenderDlg(
            long id
    ) {
        this.id = id;
        setTitle(getTitle() + id);

        controller().connect("check", this::check);

        new AuditProxy().uncheckedTenders(id)
                        .thenApplyAsync(Result::list)
                        .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void check(ActionEvent actionEvent) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {
            @Override
            public String getTitle() {
                return controller().getMessage("check-title");
            }

            @Override
            protected Collection<Map<String, Object>> load() {
                final JTable table = controller().get(JTable.class, "list");
                final TypedTableModel tableModel = (TypedTableModel) table.getModel();
                List<Map<String, Object>> retList = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("p-id", id);
                    params.put("jb-id", tableModel.getNumberByName(i, "jbId"));
                    retList.add(params);
                }
                return retList;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在执行：投标业务id - " + params.get("jb-id");
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            ) {
                new AuditProxy().checkTenders(params)
                                .thenApplyAsync(Result::map)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI).join();
            }

            private void saveCallback(
                    Map<String, Object> map,
                    Throwable t
            ) {
                if (t != null) {
                    ErrorHandler.handle(t);
                }
            }

        });
        showModel(null, dlg);
    }

    private void searchCallback(
            Collection<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            JTable table = controller().get(JTable.class, "list");
            TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.setAllRows(maps);
        }
    }
}
