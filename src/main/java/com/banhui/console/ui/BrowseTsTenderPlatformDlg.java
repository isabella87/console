package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseTsTenderPlatformDlg
        extends DialogPane {

    private volatile long auId;

    public BrowseTsTenderPlatformDlg(
            long auId
    ) {
        this.auId = auId;
        if (auId != 0) {
            setTitle(getTitle() + auId);
        }
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("search", this::search);
        controller().connect("excel", this::excel);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        final Map<String, Object> params = new HashMap<>();
        if (this.auId != 0) {
            params.put("au-id", auId);
        }
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));
        final int type = controller().getInteger("type");

        if (type != Integer.MAX_VALUE) {
            params.put("type", type);
        }
        params.put("start-date", startDate);
        params.put("end-date", endDate);
        new AccountsProxy().tsTendersPlatform(params)
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

    private void accelerateDate(
            Object event
    ) {
        final int years = controller().getInteger("accelerate-date");
        if (years >= 0) {
            DateRange dateRange = latestSomeYears(new Date(), years);
            if (dateRange != null) {
                controller().setDate("start-date", dateRange.getStart());
                controller().setDate("end-date", dateRange.getEnd());
            }
        }
    }
}
