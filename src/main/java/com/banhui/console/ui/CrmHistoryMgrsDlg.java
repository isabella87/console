package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CrmHistoryMgrsDlg
        extends DialogPane {
    public CrmHistoryMgrsDlg(
            long auId
    ) {
        setTitle(getTitle() + auId);
        new CrmProxy().queryHistoryMgrs(auId)
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
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.setAllRows(c);
        }
    }
}
