package com.banhui.console.ui;

import com.banhui.console.rpc.B2cTransProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.DialogUtils;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseB2cDetailFrame
        extends InternalFramePane {

    private final Logger logger = LoggerFactory.getLogger(BrowseB2cDetailFrame.class);

    /**
     * {@inheritDoc}
     */
    public BrowseB2cDetailFrame() {
        controller().connect("search", this::search);
        controller().connect("create", this::create);
        controller().connect("execute", this::execute);
        controller().connect("delete", this::delete);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("list", "change", this::listChanged);

        controller().call("search");

        controller().disable("execute");
        controller().disable("delete");
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final String key = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
        params.put("start-time", startDate);
        params.put("end-time", endDate);
        params.put("key", key);


        controller().disable("search");
//串行异步，链式异步
        new B2cTransProxy().queryB2cDetails(params)
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

    private void create(
            ActionEvent event
    ) {
        String remark = DialogUtils.inputText(null, "商户转账，备注：", "");
        if (remark != null && !remark.isEmpty()) {
            final Map<String, Object> params = new HashMap<>();
            params.put("remark", remark);
            new B2cTransProxy().createB2cDetail(params)
                               .thenApplyAsync(Result::map)
                               .whenCompleteAsync(this::createCallback, UPDATE_UI);
        }

    }

    private void createCallback(
            Map<String, Object> result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.insertRow(0, result);
        }
    }

    private void execute(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        final long id = tableModel.getNumberByName(selectedRow, "tbdId");

        final B2cExecutionDlg dlg = new B2cExecutionDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void delete(
            ActionEvent event
    ) {
        String confirmDeleteText = controller().formatMessage("confirm-delete-text");
        if (confirm(null, confirmDeleteText)) {
            controller().disable("delete");
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final String tbdId = tableModel.getStringByName(table.getSelectedRow(), "tbdId");

            new B2cTransProxy().deleteB2cDetail(longValue(tbdId))
                               .thenApplyAsync(Result::map)
                               .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            Map<String, Object> deletedRow,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            logger.debug("ts_b2c_detail {} delete", deletedRow);
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(deletedRow.get("tbdId"), row.get("tbdId")));
        }
    }

    private void accelerateDate(
            Object event
    ) {
        final int years = controller().getInteger("accelerate-date");
        DateRange dateRange = latestSomeYears(new Date(), years);
        if (dateRange != null) {
            controller().setDate("start-date", dateRange.getStart());
            controller().setDate("end-date", dateRange.getEnd());
        }

    }


    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            // 选中了行，并且仅选中一行。
            controller().enable("execute");
            controller().enable("delete");
        } else if (selectedRows.length > 1) {
            controller().disable("execute");
            controller().enable("delete");
        } else {
            controller().disable("execute");
            controller().disable("delete");
        }
    }
}
