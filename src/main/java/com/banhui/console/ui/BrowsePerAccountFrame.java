package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.commons.DateRange;
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
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowsePerAccountFrame
        extends BaseFramePane {
    //private volatile Collection<Map<String, Object>> lists;
    private volatile int pageNum;
    private volatile int pageIndex;
    private final int PAGE_SIZE = 2000;

    public BrowsePerAccountFrame() {
        controller().disable("account-info");

        controller().connect("list", "change", this::listChanged);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("search", this::search);
        controller().connect("account-info", this::accountInfo);
        controller().connect("account-info", this::accountInfo);
        controller().connect("next-page", this::nextPage);
        controller().connect("previous-page", this::previousPage);
        controller().connect("to-page", this::toPage);

        disablePage();

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        setTableTitleAndTableModelForExport(getTitle(), tableModel);
    }

    private void updatePage() {
        controller().enable("previous-page");
        controller().enable("next-page");
        controller().enable("to-page");
        int pageIndex = controller().getInteger("this-page");
        if (pageIndex <= 1) {
            controller().disable("previous-page");
        } else if (pageIndex >= pageNum) {
            controller().disable("next-page");
        }
    }

    private void accountInfo(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long id = tableModel.getNumberByName(selectedRow1, "auId");
        final long status = tableModel.getNumberByName(selectedRow1, "status");

        final EditPerAccountInfoDlg dlg = new EditPerAccountInfoDlg(id, status);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        controller().disable("search");
        new AccountsProxy().getAccPersonTotal(getParams())
                           .thenApplyAsync(Result::longValue)
                           .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void searchCallback(
            Long num,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            pageNum = (PAGE_SIZE + num.intValue() - 1) / PAGE_SIZE;
            controller().setText("page-num", controller().formatMessage("all-page", num, pageNum));
            search2(null, 1);
        }
    }

    private void previousPage(
            ActionEvent actionEvent
    ) {
        disablePage();
        int pageIndex = controller().getInteger("this-page") - 1;
        search2(null, pageIndex);
    }

    private void nextPage(
            ActionEvent actionEvent
    ) {
        disablePage();
        int pageIndex = controller().getInteger("this-page") + 1;
        search2(null, pageIndex);
    }

    private void toPage(
            ActionEvent actionEvent
    ) {
        int pageIndex = controller().getInteger("to-page-num");
        if (pageIndex > pageNum || pageIndex <= 0) {
            warn(null, controller().formatMessage("wrong-page", pageNum));
            controller().setText("to-page-num", null);
            return;
        }
        disablePage();
        search2(null, pageIndex);
    }


    private void search2(
            ActionEvent actionEvent,
            int pageIndex
    ) {
        Map<String, Object> param = getParams();
        this.pageIndex = pageIndex;
        param.put("pn", pageIndex);
        new AccountsProxy().queryAccPersonInfos(param)
                           .thenApplyAsync(Result::list)
                           .whenCompleteAsync(this::searchCallback2, UPDATE_UI);
    }

    private void searchCallback2(
            Collection<Map<String, Object>> map,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(map);
            controller().setInteger("this-page", pageIndex);
            updatePage();
            controller().enable("search");
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

    public Map<String, Object> getParams() {
        final int status = controller().getInteger("account-status");
        final int lockStatus = controller().getInteger("lock-status");
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
        params.put("start-time", startDate);
        params.put("end-time", endDate);
        return params;
    }

    public void disablePage() {
        controller().disable("previous-page");
        controller().disable("next-page");
        controller().disable("to-page");
    }

}
