package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowsePaymentDepositDlg
        extends DialogPane {

    private volatile long auId;

    public BrowsePaymentDepositDlg(
            long auId
    ) {
        this.auId = auId;
        if (auId != 0) {
            setTitle(getTitle() + auId);
        }
        controller().connect("search", this::search);
        controller().connect("excel", this::excel);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        final Map<String, Object> params = new HashMap<>();
        if (this.auId != 0) {
            params.put("au-id", auId);
        }
        final String tranTypeFlag = String.valueOf(controller().getInteger("tran-type-flag"));
        params.put("tran-type-flag", tranTypeFlag);
        params.put("tran-type", 0);
        new AccountsProxy().historyFundsDeposit(params)
                           .thenApplyAsync(Result::map)
                           .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                           .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Map<String, Object> map,
            Throwable t
    ) {
        if (t != null) {
            warn(null, t.getCause().getMessage());
            ErrorHandler.handle(t);
        } else {
            Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) map.get("items");
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(items);
        }
    }

    private void excel(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        new ExcelExportUtil(getTitle(), tableModel).choiceDirToSave();
    }
}
