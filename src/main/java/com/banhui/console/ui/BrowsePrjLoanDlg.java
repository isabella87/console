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

public class BrowsePrjLoanDlg extends DialogPane {

    private volatile long id;
    private Map<String, Object> row;

    public BrowsePrjLoanDlg(
            long id
    ) {
        this.id = id;
        setTitle(controller().getMessage("title") + "-" + id);
        new ProjectProxy().queryLoans(id)
                          .thenApplyAsync(Result::map)
                          .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                          .exceptionally(ErrorHandler::handle);
    }

    @Override
    protected void initUi() {
        super.initUi();
        controller().connect("loan", this::loan);
        controller().connect("refresh", this::refresh);
    }

    //执行放款
    private void loan(
            ActionEvent actionEvent
    ) {
        String confirmLoanText = controller().formatMessage("confirm-loan-text");
        String confirmText = controller().formatMessage("confirm-text");
        if (confirm(confirmLoanText, confirmText)) {
            new ProjectProxy().executeLoan(id)
                              .thenApplyAsync(Result::map);
            new ProjectProxy().queryLoans(id)
                              .thenApplyAsync(Result::map)
                              .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                              .exceptionally(ErrorHandler::handle);
        }
    }

    //刷新
    private void refresh(
            ActionEvent actionEvent
    ) {
        new BrowsePrjLoanDlg(id);
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Map<String, Object> map
    ) {
        Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) map.get("items");
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        Collection<Map<String, Object>> children = (Collection<Map<String, Object>>) map.get("children");
        final TypedTableModel tableModel2 = (TypedTableModel) controller().get(JTable.class, "list2").getModel();
        tableModel.setAllRows(items);
        tableModel2.setAllRows(children);
        double sum1 = 0L;
        double sum2 = 0L;
        for (int i = 0; i < items.size(); i++) {
            double status = tableModel.getNumberByName(i, "status");
            if (status == 0) {
                double execute = tableModel.getNumberByName(i, "amt");
                sum1 = sum1 + execute;
            } else {
                double execute = tableModel.getNumberByName(i, "amt");
                sum2 = sum2 + execute;
            }
        }
        controller().setText("progress", "已执行金额：" + sum2 + " 元  " + "总共金额: " + (sum1 + sum2) + " 元");
    }

    private void saveCallback(
            Map<String, Object> row
    ) {
        this.row = row;
        super.done(OK);
    }
}
