package com.banhui.console.ui;

import com.banhui.console.rpc.BaPrjMortgageProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;

import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseBaPrjMortgageFrame
        extends BaseFramePane {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(BrowseBaPrjMortgageFrame.class);

    /**
     * {@inheritDoc}
     */
    public BrowseBaPrjMortgageFrame() {
        controller().connect("search", this::search);
        controller().connect("create", this::create);
        controller().connect("edit", this::edit);
        controller().connect("delete", this::delete);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("list", "change", this::listChanged);

        controller().disable("edit");
        controller().disable("delete");

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        setTableTitleAndTableModelForExport(getTitle(), tableModel);
        showTipLabel();
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
        new BaPrjMortgageProxy().all(params)
                                .thenApplyAsync(Result::list)
                                .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
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

    private void create(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;

        final EditBaPrjMortgageDlg dlg = new EditBaPrjMortgageDlg(0);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
    }

    private void edit(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long id = tableModel.getNumberByName(selectedRow1, "bpmId");
        final EditBaPrjMortgageDlg dlg = new EditBaPrjMortgageDlg(id);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();

            if (row != null && !row.isEmpty()) {
                tableModel.setRow(selectedRow1, row);
            }
        }
    }

    private void delete(
            ActionEvent event
    ) {
        String confirmDeleteText = controller().formatMessage("confirm-delete-text");
        if (confirm(null, confirmDeleteText)) {
            controller().disable("delete");

            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
            final long bpmId = tableModel.getNumberByName(selectedRow1, "bpmId");
            new BaPrjMortgageProxy().del(bpmId)
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
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(deletedRow.get("bpmId"), row.get("bpmId")));
        }
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


    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            // 选中了行，并且仅选中一行。
            controller().enable("edit");
            controller().enable("delete");
        } else if (selectedRows.length > 1) {
            controller().disable("edit");
            controller().enable("delete");
        } else {
            controller().disable("edit");
            controller().disable("delete");
        }
    }
}
