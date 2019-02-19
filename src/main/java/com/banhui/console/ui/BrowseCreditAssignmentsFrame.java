package com.banhui.console.ui;

import com.banhui.console.rpc.CreditAssignmentsProxy;
import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseCreditAssignmentsFrame
        extends BaseFramePane {
    /**
     * {@inheritDoc}
     */
    public BrowseCreditAssignmentsFrame() {

        controller().connect("list", "change", this::listChanged);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("search", this::search);
        controller().connect("top", this::top);
        controller().connect("cancel-top", this::cancelTop);
        controller().connect("check-agreement", this::checkAgreement);
        controller().connect("advance-revoke", this::advanceRevoke);

        controller().disable("advance-revoke");
        controller().disable("top");
        controller().disable("check-agreement");
        controller().hide("cancel-top");

        JLabel perAmtLable = controller().get(JLabel.class, "remarks");
        perAmtLable.setForeground(Color.blue);

        TableColumnModel tcm = controller().get(JTable.class, "list").getColumnModel();
        TableColumn tiIdColumn = tcm.getColumn(21);
        TableColumn topTimeColumn = tcm.getColumn(22);
        tcm.removeColumn(tiIdColumn);
        tcm.removeColumn(topTimeColumn);

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        setTableTitleAndTableModelForExport(getTitle(), tableModel);
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final int dateType = controller().getInteger("date-type");
        final int transferStatus = controller().getInteger("transfer-status");
        final int keyType = controller().getInteger("key-type");
        final int prjType = controller().getInteger("prj-type");
        final String key = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
        params.put("start-time", startDate);
        params.put("end-time", endDate);
        params.put("date-type", dateType);
        if (prjType != Integer.MAX_VALUE) {
            params.put("prj-type", prjType);
        }
        params.put("status", transferStatus);
        params.put("key", key);
        params.put("key-type", keyType);
        controller().disable("search");
        new CreditAssignmentsProxy().queryAll(params)
                                    .thenApplyAsync(Result::list)
                                    .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                                    .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void advanceRevoke(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long pid = tableModel.getNumberByName(selectedRow1, "pId");
        final String itemName = tableModel.getStringByName(selectedRow1, "itemName");

        String confirmDeleteText = controller().formatMessage("confirm-delete-text", itemName);
        if (confirm(null, confirmDeleteText)) {
            controller().disable("advance-revoke");
            new CreditAssignmentsProxy().revoke(pid)
                                        .thenApplyAsync(Result::booleanValue)
                                        .whenCompleteAsync(this::revokeCallback, UPDATE_UI)
                                        .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
        }
    }

    private void revokeCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null || !flag) {
            prompt(null, controller().getMessage("revoke-fail"));
            ErrorHandler.handle(t);
        } else {
            prompt(null, controller().getMessage("revoke-success"));
            controller().call("search");
        }
    }


    private void checkAgreement(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long tiId = tableModel.getNumberByName(selectedRow1, "tiId");
        ChooseProtocolDlg dlg = new ChooseProtocolDlg(tiId, 48, 2);
        dlg.setFixedSize(false);
        showModel(null, dlg);

    }

    private void top(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long pid = tableModel.getNumberByName(selectedRow1, "pId");
        new ProjectProxy().goTop(pid);
        controller().call("search");
    }

    private void cancelTop(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long pid = tableModel.getNumberByName(selectedRow1, "pId");
        new ProjectProxy().revokeTop(pid);
        controller().call("search");
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
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getDateByName(i, "topTime") != null) {
                    tableModel.setValueAt(tableModel.getStringByName(i, "itemName") + "（已置顶）", i, 2);
                }
                //priceDifference 计算标价差额
                BigDecimal creditAmount = tableModel.getBigDecimalByName(i, "creditAmount");
                BigDecimal assignAmt = tableModel.getBigDecimalByName(i, "assignAmt");
                tableModel.setValueAt(creditAmount.subtract(assignAmt), i, 11);

                //REAL_AMOUNT 计算实收金额
                BigDecimal fee = tableModel.getBigDecimalByName(i, "fee");
                tableModel.setValueAt(assignAmt.subtract(fee), i, 13);

                //UNPAID_TRAN_NO 计算剩余期数
                long totalTranNo = tableModel.getNumberByName(i, "totalTranNo");
                long paidTranNo = tableModel.getNumberByName(i, "paidTranNo");
                tableModel.setValueAt(totalTranNo - paidTranNo, i, 8);
            }
        }
    }

    private void accelerateDate(
            Object event
    ) {
        final int years = controller().getInteger("accelerate-date");
        DateRange dateRange = null;
        if (years >= 0) {
            dateRange = latestSomeYears(new Date(), years);
        } else if (years == -2) {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.YEAR, 1);
            dateRange = latestSomeYears(cal.getTime(), 0);
        }
        if (dateRange != null) {
            controller().setDate("start-date", dateRange.getStart());
            controller().setDate("end-date", dateRange.getEnd());
        }

    }

    private void listChanged(
            Object event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        int[] selectedRows = table.getSelectedRows();
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        if (table.getSelectedRow() > -1) {
            final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
            final long status = tableModel.getNumberByName(selectedRow1, "status");
            final Date topTime = tableModel.getDateByName(selectedRow1, "topTime");
            if (selectedRows.length == 1) {
                if (topTime != null) {
                    controller().show("cancel-top");
                    controller().hide("top");
                    controller().enable("cancel-top");
                } else {
                    controller().hide("cancel-top");
                    controller().show("top");
                    controller().enable("top");
                }
                if (status == 1) {
                    controller().enable("check-agreement");
                } else if (status == 0) {
                    controller().enable("advance-revoke");
                } else {
                    controller().disable("advance-revoke");
                    controller().disable("check-agreement");
                }
            } else if (selectedRows.length > 1) {
                controller().disable("top");
                controller().disable("cancel-top");
                controller().disable("advance-revoke");
                controller().disable("check-agreement");
            }
        } else {
            controller().disable("top");
            controller().disable("cancel-top");
            controller().disable("advance-revoke");
            controller().disable("check-agreement");
        }
    }
}


