package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseBorSignInfoDlg
        extends DialogPane {
    private volatile long pid;

    public BrowseBorSignInfoDlg(
            long pid
    ) {
        this.pid = pid;
        if (this.pid != 0) {
            setTitle(getTitle() + pid);
        }
        controller().connect("search", this::search);
        controller().call("search");
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        new ProjectProxy().borSignInfo(pid)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                          .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
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
