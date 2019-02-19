package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseMonthReportsDlg
        extends DialogPane {
    private volatile long id;

    public BrowseMonthReportsDlg(
            long id
    ) {
        this.id = id;
        if (this.id != 0) {
            setTitle(getTitle() + id);
        }
        new AccountsProxy().monthReports(id)
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
