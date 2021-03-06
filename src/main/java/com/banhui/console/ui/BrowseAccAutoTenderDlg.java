package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.ProjectProxy;
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

public class BrowseAccAutoTenderDlg
        extends DialogPane {
    private volatile long auId;

    public BrowseAccAutoTenderDlg(
            long auId
    ) {
        this.auId = auId;
        if (this.auId != 0) {
            setTitle(getTitle() + this.auId);
        }
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("search", this::search);
    }

    private void search(
            ActionEvent actionEvent
    ) {

        final String key = controller().getText("key");
        final Date startTime = floorOfDay(controller().getDate("start-date"));
        final Date endTime = ceilingOfDay(controller().getDate("end-date"));
        final Boolean status = controller().getBoolean("status");

        final Map<String, Object> params = new HashMap<>();
        params.put("au-id", auId);
        params.put("start-time", startTime);
        params.put("end-time", endTime);
        params.put("search-key", key);
        params.put("status", status);
        controller().disable("search");

        new AccountsProxy().accAutoTender(params)
                           .thenApplyAsync(Result::list)
                           .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                           .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(maps);
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
