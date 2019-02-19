package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowsePrjInvestmentDlg extends DialogPane {

    private volatile long id;

    public BrowsePrjInvestmentDlg(
            long id
    ) {
        this.id = id;
        final Map<String, Object> params = new HashMap<>();
        if (this.id != 0) {
            params.put("p-id", id);
        }
        setTitle(getTitle() + id);
        controller().setDate("datepoint", new Date());

        controller().connect("excel", this::excel);
        controller().connect("search", this::search);
        controller().call("search");
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        final Map<String, Object> params = new HashMap<>();
        if (this.id != 0) {
            params.put("p-id", id);
        }
        params.put("datepoint", controller().getDate("datepoint"));
        new AuditProxy().queryInvests(params)
                        .thenApplyAsync(Result::list)
                        .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                        .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void excel(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        new ExcelExportUtil(getTitle(), tableModel).choiceDirToSave(getTitle());
    }

    private void searchCallback(
            Collection<Map<String, Object>> c,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(c);
        }
    }
}
