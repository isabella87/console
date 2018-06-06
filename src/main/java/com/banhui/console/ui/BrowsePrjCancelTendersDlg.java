package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;


public class BrowsePrjCancelTendersDlg extends DialogPane {

    private Map<String, Object> row;
    private volatile long id;

    public BrowsePrjCancelTendersDlg(
            long id
    ) {
        this.id = id;
        setTitle(controller().getMessage("cancelTenders") + "-" + id);
        new ProjectProxy().queryCancelTenders(id)
                          .thenApplyAsync(Result::map)
                          .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                          .exceptionally(ErrorHandler::handle);
    }

    @Override
    protected void initUi() {
        super.initUi();
        controller().connect("list", "change", this::listChanged);
        controller().disable("execute");
        controller().connect("execute", this::execute);
        controller().connect("refresh", this::refresh);
    }

    //执行流标
    private void execute(
            ActionEvent actionEvent
    ) {
        String confirmCancelTenderText = controller().formatMessage("confirm-cancel-tender");
        String confirmText = controller().formatMessage("confirm-text");
        if (confirm(confirmCancelTenderText, confirmText)) {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final int selectedRow = table.getSelectedRow();
            final long tctId = tableModel.getNumberByName(selectedRow, "tctId");
            new ProjectProxy().execute(tctId)
                              .thenApplyAsync(Result::map);
            new ProjectProxy().queryCancelTenders(id)
                              .thenApplyAsync(Result::map)
                              .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                              .exceptionally(ErrorHandler::handle)
                              .thenAcceptAsync(v -> controller().enable("execute"), UPDATE_UI);
        }
    }

    //刷新
    private void refresh(
            ActionEvent actionEvent
    ) {
        new BrowsePrjCancelTendersDlg(id);
        controller().disable("execute");
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Map<String, Object> map
    ) {
        Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) map.get("items");
        Collection<Map<String, Object>> children = (Collection<Map<String, Object>>) map.get("children");
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(items);
        final TypedTableModel tableModel2 = (TypedTableModel) controller().get(JTable.class, "list2").getModel();
        tableModel2.setAllRows(children);
//        double sum1=0L;
//        double sum2=0L;
//        for (int i = 0;i<items.size();i++){
//            double status = tableModel.getNumberByName(i, "status");
//            if(status==0){
//                double execute = tableModel.getNumberByName(i,"amt");
//                sum1 = sum1+execute;
//            }else {
//                double execute = tableModel.getNumberByName(i,"amt");
//                sum2 = sum2+execute;
//            }
//        }
//        controller().setText("progress","已执行金额："+sum2+" 元  "+"总共金额: "+(sum1+sum2)+" 元");
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length >= 1) {
            controller().enable("execute");
        }
    }

    private void saveCallback(
            Map<String, Object> row
    ) {
        this.row = row;
        super.done(OK);
    }
}
