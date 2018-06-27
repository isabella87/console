package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowsePrjLoanDlg extends DialogPane {

    private volatile long id;
    private Collection<Map<String, Object>> children;

    public BrowsePrjLoanDlg(
            long id
    ) {
        this.id = id;
        setTitle(getTitle() + id);
        new AuditProxy().queryLoans(id)
                        .thenApplyAsync(Result::map)
                        .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                        .exceptionally(ErrorHandler::handle);
        JLabel perAmtLable = controller().get(JLabel.class, "progress");
        perAmtLable.setForeground(Color.red);

        controller().connect("list", "change", this::listChanged);
        controller().connect("loan", this::loan);
        controller().connect("refresh", this::refresh);
        controller().connect("excel", this::excel);
    }

    private void excel(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        new ExcelUtil(getTitle(), tableModel).choiceDirToSave();
    }

    //执行放款
    private void loan(
            ActionEvent actionEvent
    ) {
        String confirmLoanText = controller().formatMessage("confirm-loan-text");
        String confirmText = controller().formatMessage("confirm-text");
        if (confirm(this.getOwner(), confirmLoanText, confirmText)) {
            new AuditProxy().executeLoan(id)
                            .thenApplyAsync(Result::map);
            new AuditProxy().queryLoans(id)
                            .thenApplyAsync(Result::map)
                            .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                            .exceptionally(ErrorHandler::handle);
        }
    }

    //刷新
    private void refresh(
            ActionEvent actionEvent
    ) {
        new AuditProxy().queryLoans(id)
                        .thenApplyAsync(Result::map)
                        .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                        .exceptionally(ErrorHandler::handle);
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Map<String, Object> map
    ) {
        Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) map.get("items");
        children = (Collection<Map<String, Object>>) map.get("children");
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(items);

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
        controller().setText("progress", controller().formatMessage("progress-text", sum2, (sum1 + sum2)));
    }

    private void listChanged(
            Object event
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        int selectRow = table.getSelectedRow();

        final TypedTableModel detailTableModel = (TypedTableModel) controller().get(JTable.class, "detail").getModel();
        Collection<Map<String, Object>> child = new ArrayList<>();

        if (table.getSelectedRow() > -1) {
            long tlId = tableModel.getNumberByName(selectRow, "tlId");
            for (Map<String, Object> item : children) {
                if (Long.valueOf(item.get("tsId").toString()) == tlId) {
                    child.add(item);
                }
            }
        }
        detailTableModel.setAllRows(child);
    }

}
