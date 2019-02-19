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
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CrmFastAssignDlg
        extends DialogPane {
    private volatile List<Long> ids;
    private volatile int size;
    private volatile int index;

    public CrmFastAssignDlg(
            List<Long> ids
    ) {
        this.ids = new ArrayList<>();
        this.ids = ids;
        this.size = ids.size();
        controller().disable("do-reset");
        controller().connect("list", "change", this::listChanged);
        controller().connect("do-reset", this::resetManager);

        final Map<String, Object> params = new HashMap<>();
        params.put("if-self", false);
        new CrmProxy().getAllMgrRelations(params)
                      .thenApplyAsync(Result::list)
                      .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }


    private void resetManager(
            ActionEvent actionEvent
    ) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

            @Override
            public String getTitle() {
                return controller().getMessage("reset-title");
            }

            @Override
            protected Collection<Map<String, Object>> load() {
                final JTable table = controller().get(JTable.class, "list");
                final TypedTableModel tableModel = (TypedTableModel) table.getModel();
                final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
                String uName = tableModel.getStringByName(selectedRow1, "uName");
                if (uName.equals("我自己")) {
                    uName = "+";
                }
                List<Map<String, Object>> retList = new ArrayList<>();
                for (long id : ids) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("au-id", id);
                    params.put("u-name", uName);
                    retList.add(params);
                }
                return retList;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在执行:(客户ID- " + params.get("au-id") + ",客户经理-" + params.get("u-name");
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            ) {
                setIndex(i);
                new CrmProxy().bindRegUserWithMgr(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::updateCallBack, UPDATE_UI);
            }

            private void updateCallBack(
                    Long num,
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
            Collection<Map<String, Object>> c,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final Map<String, Object> params = new HashMap<>();
            params.put("uName", "我自己");
            c.add(params);
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(c);
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("do-reset");
        } else if (selectedRows.length > 1) {
            controller().disable("do-reset");
        } else {
            controller().disable("do-reset");
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
