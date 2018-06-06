package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
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
        params.put("datepoint", new Date());
        setTitle(controller().getMessage("title") + "-" + id);
        new ProjectProxy().queryInvests(params, id)
                          .thenApplyAsync(Result::list)
                          .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                          .exceptionally(ErrorHandler::handle);
    }

    @Override
    protected void initUi() {
        super.initUi();
    }


    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }
}
