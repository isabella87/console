package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.AuthenticationProxy;
import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static com.banhui.console.ui.InputUtils.today;
import static com.banhui.console.ui.InputUtils.tomorrow;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseProjectsFrame
        extends BaseFramePane {
    /**
     * {@inheritDoc}
     */
    private volatile String userName;

    public BrowseProjectsFrame() {
        controller().connect("search", this::search);
        controller().connect("audit", this::audit);
        controller().connect("repay", this::repay);
        controller().connect("view", this::view);
        controller().connect("edit", this::edit);
        controller().connect("create", this::create);
        controller().connect("delete", this::delete);
        controller().connect("repay-history", this::repayHistory);
        controller().connect("loan-history", this::loanHistory);
        controller().connect("list", "change", this::listChanged);
        controller().connect("hide", this::hidePrj);
        controller().connect("cancel-hide", this::cancelHide);
        controller().connect("lock", this::lock);
        controller().connect("unlock", this::unlock);
        controller().connect("top", this::top);
        controller().connect("cancel-top", this::cancelTop);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("bonds-man", this::getBondsMan);
        controller().connect("modify-bonds-man", this::modifyBondsman);

        controller().disable("audit");
        controller().disable("view");
        controller().disable("edit");
        controller().disable("repay");
        controller().disable("delete");
        controller().disable("hide");
        controller().disable("top");

        controller().hide("cancel-hide");
        controller().hide("cancel-top");
        controller().hide("modify-bonds-man");

        controller().setNumber("status", 40L);
        controller().setNumber("locked", 0L);
        JTable jTable = controller().get(JTable.class, "list");
        doubleClickEvent(jTable);
        TableColumnModel tcm = jTable.getColumnModel();
        TableColumn visibleColumn = tcm.getColumn(18);
        TableColumn topTimeColumn = tcm.getColumn(19);
        TableColumn lockTimeColumn = tcm.getColumn(20);
        tcm.removeColumn(visibleColumn);
        tcm.removeColumn(topTimeColumn);
        tcm.removeColumn(lockTimeColumn);

        final JTable table = controller().get(JTable.class, "list");
        table.setDragEnabled(true);

        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        setTableTitleAndTableModelForExport(getTitle(), tableModel);

        new AuthenticationProxy().current()
                                 .thenApply(Result::map)
                                 .whenCompleteAsync(this::userInfo, UPDATE_UI);
    }

    private void modifyBondsman(ActionEvent actionEvent) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long pid = tableModel.getNumberByName(selectedRow1, "pId");
        final ChooseBondsManDlg dlg = new ChooseBondsManDlg(pid);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void getBondsMan(ActionEvent actionEvent) {
        final BrowseBondsManDlg dlg = new BrowseBondsManDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void unlock(ActionEvent actionEvent) {
        controller().enable("unlock");
        ProgressDialog pd = new ProgressDialog(new ProgressDialog.ProgressRunner<Long>() {
            @Override
            public String getTitle() {

                return "解锁";
            }

            @Override
            protected Collection<Long> load() {
                JTable jTable = controller().get(JTable.class, "list");
                TypedTableModel typedTableModel = (TypedTableModel) jTable.getModel();
                Collection<Long> pids = new ArrayList<>();
                int[] rows = jTable.getSelectedRows();
                for (int row : rows) {
                    row = jTable.convertRowIndexToModel(row);
                    pids.add(typedTableModel.getNumberByName(row, "pId"));
                }

                return pids;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Long id
            ) {
                return "已执行到" + id;
            }

            @Override
            protected void execute(
                    int i,
                    Long id
            )
                    throws Exception {
                new AuditProxy().unlockPrj(id)
                                .thenApplyAsync(Result::map);

            }
        });
        if(showModel(null, pd) == DialogPane.OK ||showModel(null, pd) == DialogPane.CANCEL){
            controller().enable("unlock");
            controller().call("search");
        }

    }

    private void lock(ActionEvent actionEvent) {
        controller().disable("lock");
        ProgressDialog pd = new ProgressDialog(new ProgressDialog.ProgressRunner<Long>() {
            @Override
            public String getTitle() {

                return "锁定";
            }

            @Override
            protected Collection<Long> load() {
                JTable jTable = controller().get(JTable.class, "list");
                TypedTableModel typedTableModel = (TypedTableModel) jTable.getModel();
                Collection<Long> pids = new ArrayList<>();
                int[] rows = jTable.getSelectedRows();
                for (int row : rows) {
                    row = jTable.convertRowIndexToModel(row);
                    pids.add(typedTableModel.getNumberByName(row, "pId"));
                }

                return pids;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Long id
            ) {
                return "已执行到" + id;
            }

            @Override
            protected void execute(
                    int i,
                    Long id
            )
                    throws Exception {
                new AuditProxy().lockPrj(id)
                                .thenApplyAsync(Result::map);

            }
        });
        if(showModel(null, pd) == DialogPane.OK ||showModel(null, pd) == DialogPane.CANCEL){
            controller().enable("lock");
            controller().call("search");
        }
    }

//    public void mouseClicked(MouseEvent e) {
//        if (e.getClickCount() == 2) {
//            JTable jTable = controller().get(JTable.class, "list");
//            final TypedTableModel typedTableModel = (TypedTableModel) jTable.getModel();
//            int row = jTable.convertRowIndexToModel(jTable.getSelectedRow());
//            jTable.setColumnSelectionInterval(0, jTable.getColumnCount());
//        }
//    }

    private void userInfo(
            Map<String, Object> user,
            Throwable throwable
    ) {
        if (throwable != null) {
            ErrorHandler.handle(throwable);
        }
        this.userName = stringValue(user, "userName");
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

    private void hidePrj(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long pid = tableModel.getNumberByName(selectedRow1, "pId");
        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", pid);
        params.put("visible", false);
        new ProjectProxy().updatePrjVisible(params);
        controller().call("search");
    }

    private void cancelHide(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long pid = tableModel.getNumberByName(selectedRow1, "pId");
        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", pid);
        params.put("visible", true);
        new ProjectProxy().updatePrjVisible(params);
        controller().call("search");
    }

    private void loanHistory(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        final BrowseLoanHistoryDlg dlg = new BrowseLoanHistoryDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void repayHistory(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        final BrowseRepayHistoryDlg dlg = new BrowseRepayHistoryDlg();
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void repay(ActionEvent actionEvent) {

        assertUIThread();

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long pId = tableModel.getNumberByName(selectedRow1, "pId");
        final long status = tableModel.getNumberByName(selectedRow1, "status");

        EditPrjRepayDlg dlg = new EditPrjRepayDlg(pId, status);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final int dateType = controller().getInteger("date-type");
        final int status = Integer.valueOf(controller().getText("status"));
        final int type = Integer.valueOf(controller().getText("type"));
        final int keyType = controller().getInteger("key-type");
        final String key = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));
        final Boolean locked = controller().getBoolean("locked");

        final Map<String, Object> params = new HashMap<>();
        params.put("locked", locked);

        switch (dateType) {
            case 1:
                params.put("start-time", startDate);
                params.put("end-time", endDate);
                break;
            case 2:
                params.put("on-line-start-time", startDate);
                params.put("on-line-end-time", endDate);
                break;
            case 3:
                params.put("loan-start-time", startDate);
                params.put("loan-end-time", endDate);
                break;
            case 4:
                params.put("capital-repay-start-time", startDate);
                params.put("capital-repay-end-time", endDate);
                break;
            case 5:
                params.put("clear-start-time", startDate);
                params.put("clear-end-time", endDate);
                break;
            case 6:
                params.put("repay-start-time", startDate);
                params.put("repay-end-time", endDate);
                break;
        }

        if (status != Integer.MAX_VALUE) {
            params.put("status", status);
        }
        if (type != Integer.MAX_VALUE) {
            params.put("type", type);
        }

        switch (keyType) {
            case 1:
                params.put("item-no-key", key);
                break;
            case 2:
                params.put("item-name-key", key);
                break;
            case 3:
                params.put("financier-key", key);
                break;
            case 4:
                params.put("creator-key", key);
                break;
        }
        controller().disable("search");
        new ProjectProxy().allProjects(params)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c,
            Throwable t
    ) {
        controller().enable("search");
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            for (Map<String, Object> data : c) {
                if (longValue(data, "visible") == 0) {
                    data.put("itemName", stringValue(data, "itemName") + "(已隐藏)");
                }
                if (dateValue(data, "topTime") != null) {
                    data.put("itemName", stringValue(data, "itemName") + "(已置顶)");
                }
                if (dateValue(data, "lockedTime") != null) {
                    data.put("itemName", stringValue(data, "itemName") + "(已锁定)");
                }
            }
            tableModel.setAllRows(c);
        }
    }

    private void view(
            ActionEvent event
    ) {
        assertUIThread();
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long pId = tableModel.getNumberByName(selectedRow1, "pId");
        final EditProjectsDlg dlg = new EditProjectsDlg(pId, 0);
        final long type = tableModel.getNumberByName(selectedRow1, "status");
        if (type == 40) {
            dlg.controller().enable("delay");
        }
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            tableModel.setRow(selectedRow1, row);
        }
    }

    private void create(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;
        final CreateProjectFrame dlg = new CreateProjectFrame();
        dlg.setFixedSize(false);
        showModel(null, dlg);
        long pId = dlg.getId();
        if (pId != 0) {
            final EditProjectsDlg dlg2 = new EditProjectsDlg(pId, 1);
            dlg2.setFixedSize(false);
            if (showModel(null, dlg2) == DialogPane.OK) {
                Map<String, Object> row = dlg2.getResultRow();
                if (row != null && !row.isEmpty()) {
                    tableModel.insertRow(selectedRow, row);
                }
            }
        }
    }

    private void edit(
            ActionEvent event
    ) {
        assertUIThread();
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        if (selectedRow1 < 0) {
            return;
        }
        final long pId = tableModel.getNumberByName(selectedRow1, "pId");
        final EditProjectsDlg dlg = new EditProjectsDlg(pId, 1);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            tableModel.setRow(selectedRow1, row);
        }
    }

    private void delete(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long pId = tableModel.getNumberByName(table.getSelectedRow(), "pId");

        String confirmDeleteText = controller().formatMessage("confirm-delete-text", pId);
        if (confirm(null, confirmDeleteText)) {
            controller().disable("delete");
            new ProjectProxy().deletePrjLoan(pId)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            Long pid,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(pid.toString(), row.get("pId").toString()));
        }
    }

    private void audit(
            ActionEvent event
    ) {
        assertUIThread();
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long id = tableModel.getNumberByName(selectedRow1, "pId");
        final long status = tableModel.getNumberByName(selectedRow1, "status");
        final double amt = tableModel.getFloatByName(selectedRow1, "amt");
        final double investedAmt = tableModel.getFloatByName(selectedRow1, "investedAmt");
        final Date extensionDate = tableModel.getDateByName(selectedRow1, "extensionDate");
        final Date timeoutDate = tableModel.getDateByName(selectedRow1, "timeOutDate");

        final AuditProjectsDlg dlg = new AuditProjectsDlg(id, status, amt, investedAmt,timeoutDate,extensionDate);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            controller().call("search");
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
            switch (years) {
                case -1:
                    EditDateTimeOptionDlg dlg = new EditDateTimeOptionDlg();
                    dlg.setFixedSize(false);
                    if (showModel(null, dlg) == DialogPane.OK) {
                        dateRange = dlg.getDateRange();
                    }
                    break;
                case -2:
                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.add(Calendar.YEAR, 1);
                    dateRange = latestSomeYears(cal.getTime(), 0);
                    break;
                case -3:
                    dateRange = today(new Date());
                    break;
                case -4:
                    dateRange = tomorrow(new Date());
                    break;
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
        final JTable table = controller().get(JTable.class, "list");
        int[] selectedRows = table.getSelectedRows();
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.getSelectedRow();
        if (selectedRow1 > -1) {
            final long status = tableModel.getNumberByName(selectedRow1, "status");
            final long visible = tableModel.getNumberByName(selectedRow1, "visible");
            final Date topTime = tableModel.getDateByName(selectedRow1, "topTime");
            //final Date lockedTime = tableModel.getDateByName(selectedRow1, "lockedTime");
            if (visible == 1) {
                controller().hide("cancel-hide");
                controller().show("hide");
            } else {
                controller().hide("hide");
                controller().show("cancel-hide");
            }
            if (topTime != null) {
                controller().show("cancel-top");
                controller().hide("top");
            } else {
                controller().hide("cancel-top");
                controller().show("top");
            }
            if (selectedRows.length == 1) {
                controller().enable("hide");
                controller().enable("top");
                controller().enable("cancel-hide");
                controller().enable("cancel-top");
                controller().enable("audit");
                controller().enable("modify-bonds-man");
                // 选中了行，并且仅选中一行。
                if (status == 0) {
                    controller().enable("delete");
                    controller().show("edit");
                    controller().enable("edit");
                    controller().hide("view");
                } else {
                    if (!userName.equals("admin")) {
                        controller().disable("delete");
                        controller().hide("edit");
                        controller().show("view");
                        controller().enable("view");
                    } else {
                        controller().enable("delete");
                        controller().show("edit");
                        controller().hide("view");
                        controller().enable("edit");
                    }
                    if (status >= 90) {
                        controller().enable("repay");
                    } else {
                        controller().disable("repay");
                    }
                    if (status >= 40) {
                        controller().show("modify-bonds-man");
                    } else {
                        controller().hide("modify-bonds-man");
                    }
                }
            } else if (selectedRows.length > 1) {
                controller().disable("view");
                controller().disable("edit");
                controller().disable("audit");
                controller().disable("repay");
                controller().disable("hide");
                controller().disable("top");
                controller().disable("cancel-hide");
                controller().disable("cancel-top");
                controller().hide("modify-bonds-man");
            }
        } else {
            controller().disable("view");
            controller().disable("edit");
            controller().disable("delete");
            controller().disable("audit");
            controller().disable("repay");
            controller().disable("hide");
            controller().disable("top");
            controller().disable("cancel-hide");
            controller().disable("cancel-top");
            controller().hide("modify-bonds-man");
        }
    }
}
