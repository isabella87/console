package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseRepayHistoryDlg
        extends DialogPane {
    public BrowseRepayHistoryDlg() {
        new ProjectProxy().queryRepayHistory()
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
            JTable table1 = controller().get(JTable.class, "list");
            TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
            tableModel1.setAllRows(c);
        }
    }
}
