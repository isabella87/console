package com.banhui.console.ui;

import com.banhui.console.rpc.BaPrjMortgageProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class ChoosePrjMortgageDlg
        extends DialogPane {

    private volatile long bpmId;
    private volatile String name;
    private volatile String showName;
    private String listName;

    public ChoosePrjMortgageDlg(
    ) {
        final Map<String, Object> params = new HashMap<>();

        controller().disable("ok");
        controller().connect("listMortgage", "change", this::listChanged);
        controller().connect("search", this::search);

        listName = "listMortgage";
        new BaPrjMortgageProxy().all(params)
                                .thenApplyAsync(Result::list)
                                .whenCompleteAsync(this::searchCallback, UPDATE_UI);


    }

    private void searchCallback(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        JTable table1 = controller().get(JTable.class, listName);
        TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
        tableModel1.setAllRows(maps);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", controller().getText("key"));
        new BaPrjMortgageProxy().all(params)
                               .thenApplyAsync(Result::list)
                               .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");
            JTable table1 = controller().get(JTable.class, listName);
            TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
            final int selectedRow = table1.getSelectedRow();
            this.bpmId = tableModel1.getNumberByName(selectedRow, "bpmId");
            this.name = tableModel1.getStringByName(selectedRow, "ownerName");
            this.showName = tableModel1.getStringByName(selectedRow, "ownerShowName");
            super.done(OK);
        } else {
            super.done(result);
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, listName).getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("ok");
        } else if (selectedRows.length > 1) {
            controller().disable("ok");
        } else {
            controller().disable("ok");
        }
    }


    @Override
    public String getName() {
        return name;
    }

    public String getShowName() {
        return showName;
    }


    public long getBpmId() {
        return bpmId;
    }
}
