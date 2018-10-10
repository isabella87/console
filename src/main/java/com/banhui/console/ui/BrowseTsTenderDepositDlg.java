package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseTsTenderDepositDlg
        extends DialogPane {

    private volatile long auId;

    public BrowseTsTenderDepositDlg(
            long auId
    ) {
        this.auId = auId;
        if (auId != 0) {
            setTitle(getTitle() + auId);
        }
        controller().connect("search", this::search);
        controller().connect("excel", this::excel);
        controller().call("search");
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        final Map<String, Object> params = new HashMap<>();
        if (this.auId != 0) {
            params.put("au-id", auId);
        }
        final int status = controller().getInteger("status");
        if (status == 1) {
            params.put("status", true);
        } else {
            params.put("status", false);
        }
        new AccountsProxy().tsTendersDeposit(params)
                           .thenApplyAsync(Result::list)
                           .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                           .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Collection<Map<String, Object>> c,
            Throwable t
    ) {
        if (t != null) {
            warn(null, t.getCause().getMessage());
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(c);
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
