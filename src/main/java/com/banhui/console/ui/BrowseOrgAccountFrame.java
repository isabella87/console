package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseOrgAccountFrame
        extends InternalFramePane {

    public BrowseOrgAccountFrame() {
        controller().disable("account-info");

        controller().connect("list", "change", this::listChanged);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("search", this::search);
        controller().connect("account-info", this::accountInfo);
    }

    private void accountInfo(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        final long id = tableModel.getNumberByName(selectedRow, "auId");

        final EditOrgAccountInfoDlg dlg = new EditOrgAccountInfoDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        assertUIThread();

        final int status = controller().getInteger("account-status");
        final int lockStatus = controller().getInteger("lock-status");
        final int dateType = controller().getInteger("date-type");
        final String searchKey = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
        if (status != Integer.MAX_VALUE) {
            params.put("status", status);
        }
        if (lockStatus != Integer.MAX_VALUE) {
            params.put("locked-status", lockStatus);
        }
        params.put("search-key", searchKey);
        if (dateType == 1) {
            params.put("start-reg-time", startDate);
            params.put("end-reg-time", endDate);
        } else {
            params.put("start-audit-time", startDate);
            params.put("end-audit-time", endDate);
        }
        controller().disable("search");

        new AccountsProxy().queryAccOrgInfo(params)
                           .thenApplyAsync(Result::list)
                           .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                           .exceptionally(ErrorHandler::handle)
                           .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }


    private void accelerateDate(
            Object event
    ) {
        final int years = controller().getInteger("accelerate-date");
        if (years != -1) {
            DateRange dateRange = latestSomeYears(new Date(), years);
            if (dateRange != null) {
                controller().setDate("start-date", dateRange.getStart());
                controller().setDate("end-date", dateRange.getEnd());
            }
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("account-info");
        } else if (selectedRows.length > 1) {
            controller().disable("account-info");
        } else {
            controller().disable("account-info");
        }
    }
}
