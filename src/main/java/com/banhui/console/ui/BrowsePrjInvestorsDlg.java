package com.banhui.console.ui;


import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowsePrjInvestorsDlg extends DialogPane {

    private volatile long id;

    public BrowsePrjInvestorsDlg(
            long id
    ) {
        this.id = id;
        setTitle(getTitle() + id);
        controller().connect("refresh", this::refresh);
        controller().call("refresh");
    }

    private void refresh(
            ActionEvent actionEvent
    ) {
        new AuditProxy().queryInvestorInfosByPId(id)
                        .thenApplyAsync(Result::list)
                        .whenCompleteAsync(this::searchCallback, UPDATE_UI);
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
