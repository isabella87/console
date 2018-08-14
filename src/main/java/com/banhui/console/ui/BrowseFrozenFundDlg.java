package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class BrowseFrozenFundDlg
        extends DialogPane {
    private volatile long id;

    public BrowseFrozenFundDlg(
            long id
    ) {
        this.id = id;
        if (this.id != 0) {
            setTitle(getTitle() + id);
        }
        controller().disable("cancel-frozen");
        controller().connect("search", this::search);
        controller().connect("cancel-frozen", this::cancelFrozen);
        controller().connect("list", "change", this::listChanged);
    }

    private void cancelFrozen(
            ActionEvent actionEvent
    ) {
        controller().disable("cancel-frozen");
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        final String orderId = tableModel.getStringByName(selectedRow, "orderId");
        final Map<String, Object> params = new HashMap<>();
        if (orderId != null) {
            params.put("old-order-id", orderId);
        }
        new AccountsProxy().unfrozen(params)
                           .thenApplyAsync(Result::booleanValue)
                           .whenCompleteAsync(this::unFrozenCallback, UPDATE_UI)
                           .thenAcceptAsync(v -> controller().enable("cancel-frozen"), UPDATE_UI);
    }

    private void unFrozenCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            warn(this.getOwner(), t.getCause().getMessage());
            ErrorHandler.handle(t);
        } else if (flag) {
            prompt(this.getOwner(), controller().getMessage("unfrozen-success"));
        } else {
            prompt(this.getOwner(), controller().getMessage("unfrozen-fail"));
        }
    }

    private void search(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        controller().disable("search");
        final Map<String, Object> params = new HashMap<>();
        if (this.id != 0) {
            params.put("au-id", id);
        }
        allDone(new AccountsProxy().frozenFund(id),
                new AccountsProxy().frozenDetail(params)
        ).thenApply(results -> new Object[]{
                results[0].map(),
                results[1].list()
        }).whenCompleteAsync(this::searchCallback, UPDATE_UI)
         .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Object[] results,
            Throwable t
    ) {
        if (t != null) {
            warn(this.getOwner(), t.getCause().getMessage());
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.setAllRows((Collection<Map<String, Object>>) results[1]);

//            double sum1 = 0D;
//            for (int i = 0; i < tableModel.getRowCount(); i++) {
//                double execute = tableModel.getNumberByName(i, "txAmount");
//                sum1 = sum1 + execute;
//            }
//            controller().setText("progress", controller().formatMessage("frozen-amt", sum1));
            BigDecimal sum = decimalValue((Map) results[0], "currBal");
            controller().setText("progress", controller().formatMessage("frozen-amt", sum));
        }
    }


    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("cancel-frozen");
        } else if (selectedRows.length > 1) {
            controller().disable("cancel-frozen");
        } else {
            controller().disable("cancel-frozen");
        }
    }
}
