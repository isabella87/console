package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CrmMonthStatisticsDlg
        extends DialogPane {
    private volatile String uName;
    private volatile Date datepoint;

    public CrmMonthStatisticsDlg(
            String uName,
            Date datepoint
    ) {
        this.uName = uName;
        this.datepoint = datepoint;

        setTitle(getTitle() + uName);
        controller().disable("history-detail");
        controller().setDate("datepoint", datepoint);
        controller().readOnly("datepoint", true);

        controller().connect("statistics", this::doStatistics);
        controller().connect("excel", this::excel);
        controller().connect("history-detail", this::historyDetail);
        controller().connect("list", "change", this::listChanged);
    }

    private void historyDetail(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long id = tableModel.getNumberByName(selectedRow1, "auId");

        BrowseHistoryInvestsDlg dlg = new BrowseHistoryInvestsDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void doStatistics(
            ActionEvent actionEvent
    ) {
        controller().disable("statistics");
        final Map<String, Object> params = new HashMap<>();
        params.put("u-name", uName);
        params.put("datepoint", datepoint);
        new CrmProxy().queryUserMonthByName(params)
                      .thenApplyAsync(Result::list)
                      .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                      .thenAcceptAsync(v -> controller().enable("statistics"), UPDATE_UI);
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

    private void excel(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        new ExcelExportUtil(getTitle(), tableModel).choiceDirToSave(getTitle());
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("history-detail");
        } else if (selectedRows.length > 1) {
            controller().disable("history-detail");
        } else {
            controller().disable("history-detail");
        }
    }
}
