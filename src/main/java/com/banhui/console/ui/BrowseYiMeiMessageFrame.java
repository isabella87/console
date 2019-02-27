package com.banhui.console.ui;

import com.banhui.console.rpc.MessageProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseYiMeiMessageFrame
        extends BaseFramePane {

    private final Logger logger = LoggerFactory.getLogger(BrowseYiMeiMessageFrame.class);
    private volatile int pageNum;
    private volatile int pageIndex;
    private final int PAGE_SIZE = 2000;

    /**
     * {@inheritDoc}
     */
    public BrowseYiMeiMessageFrame() {
        controller().connect("search", this::search);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("next-page", this::nextPage);
        controller().connect("previous-page", this::previousPage);
        controller().connect("to-page", this::toPage);

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        setTableTitleAndTableModelForExport(getTitle(), tableModel);
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();
        controller().disable("search");
        new MessageProxy().getYmMsgTotal(getParams())
                          .thenApplyAsync(Result::longValue)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void accelerateDate(
            Object event
    ) {
        final int years = controller().getInteger("accelerate-date");
        DateRange dateRange = null;
        if (years >= 0) {
            dateRange = latestSomeYears(new Date(), years);
        } else {
            EditDateTimeOptionDlg dlg = new EditDateTimeOptionDlg();
            dlg.setFixedSize(false);
            if (showModel(null, dlg) == DialogPane.OK) {
                dateRange = dlg.getDateRange();
            }
        }
        if (dateRange != null) {
            controller().setDate("start-date", dateRange.getStart());
            controller().setDate("end-date", dateRange.getEnd());
        }
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

    private void search2(
            ActionEvent actionEvent,
            int pageIndex
    ) {
        Map<String, Object> param = getParams();
        this.pageIndex = pageIndex;
        param.put("pn", pageIndex);
        new MessageProxy().queryYmMsgs(param)
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

    public Map<String, Object> getParams() {
        final String key = controller().getText("search-key");
        final String mobile = controller().getText("mobile");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
        params.put("start-time", startDate);
        params.put("end-time", endDate);
        params.put("search-key", key);
        params.put("mobile", mobile);
        params.put("count", longValue("500"));
        return params;
    }

    public void disablePage() {
        controller().disable("previous-page");
        controller().disable("next-page");
        controller().disable("to-page");
    }
}
