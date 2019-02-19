package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
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
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowsePrjLoanDlg extends DialogPane {

    private volatile long id;
    private Collection<Map<String, Object>> children;

    public BrowsePrjLoanDlg(
            long id,
            int role
    ) {
        this.id = id;
        setTitle(getTitle() + id);

        JLabel perAmtLable = controller().get(JLabel.class, "progress");
        perAmtLable.setForeground(Color.red);
        controller().disable("loan");

        if (role == 80) {
            controller().enable("loan");
        }
        controller().connect("list", "change", this::listChanged);
        controller().connect("loan", this::loan);
        controller().connect("refresh", this::refresh);
        controller().connect("excel", this::excel);
        controller().call("refresh");
    }

    private void excel(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        new ExcelExportUtil(getTitle(), tableModel).choiceDirToSave(getTitle());
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
                            .whenCompleteAsync(this::searchCallback, UPDATE_UI);
        }
    }

    private void refresh(
            ActionEvent actionEvent
    ) {
        controller().disable("refresh");
        new AuditProxy().queryLoans(id)
                        .thenApplyAsync(Result::map)
                        .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                        .thenAcceptAsync(v -> controller().enable("refresh"), UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Map<String, Object> map,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) map.get("items");
            children = (Collection<Map<String, Object>>) map.get("children");
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(items);

            double sum1 = 0D;
            double sum2 = 0D;
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
    }

    private void listChanged(
            Object event
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        final TypedTableModel detailTableModel = (TypedTableModel) controller().get(JTable.class, "detail").getModel();
        Collection<Map<String, Object>> child = new ArrayList<>();
        if (table.getSelectedRow() > -1) {
            final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
            long tlId = tableModel.getNumberByName(selectedRow1, "tlId");
            for (Map<String, Object> item : children) {
                if (Long.valueOf(item.get("tsId").toString()) == tlId) {
                    child.add(item);
                }
            }
        }
        detailTableModel.setAllRows(child);
    }

}
