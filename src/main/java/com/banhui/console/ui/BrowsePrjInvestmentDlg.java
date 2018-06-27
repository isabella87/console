package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.prompt;
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
        params.put("datepoint", new Date());
        setTitle(getTitle() + id);
        controller().connect("excel", this::excel);
        //controller().connect("search", this::search);
        new AuditProxy().queryInvests(params)
                        .thenApplyAsync(Result::list)
                        .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                        .exceptionally(ErrorHandler::handle);
    }

//    private void search(
//            ActionEvent actionEvent
//    ) {
//        final Map<String, Object> params = new HashMap<>();
//        if (this.id != 0) {
//            params.put("p-id", id);
//        }
//        params.put("datepoint", controller().getDate("datepoint"));
//        new AuditProxy().queryInvests(params)
//                        .thenApplyAsync(Result::list)
//                        .thenAcceptAsync(this::searchCallback, UPDATE_UI)
//                        .exceptionally(ErrorHandler::handle);
//    }

    private void excel(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        new ExcelUtil(getTitle(), tableModel).choiceDirToSave();
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }
}
