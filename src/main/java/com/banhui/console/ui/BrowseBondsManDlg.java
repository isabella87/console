package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseBondsManDlg
        extends DialogPane {

    public BrowseBondsManDlg() {
        controller().connect("search", this::search);
        controller().connect("excel", this::excel);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        final Map<String, Object> params = new HashMap<>();
        final int status = controller().getInteger("status");
        final String key = controller().getText("search-key");
        if (status != Integer.MAX_VALUE) {
            params.put("status", status);
        }
        params.put("search-key", key);
        new ProjectProxy().bondsmanList(params)
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
