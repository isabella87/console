package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.SysProxy;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseMyClientFrame
        extends InternalFramePane {
    private volatile Object[] ids;
    private volatile List<Map<String, Object>> lists;
    private volatile int size;
    private volatile int index;

    public BrowseMyClientFrame() {
        controller().readOnly("u-name", true);
        controller().setText("u-name", "全部");
        controller().disable("acc-info");
        controller().setDate("datepoint", new Date());

        controller().connect("list", "change", this::listChanged);
        controller().connect("search", this::search);
        controller().connect("choose-manager", this::chooseManager);
        controller().connect("acc-info", this::browseAccInfo);
    }

    private void chooseManager(
            ActionEvent actionEvent
    ) {
        CrmChooseManagerDlg dlg = new CrmChooseManagerDlg();
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            String uName = dlg.getuName();
            if (!uName.isEmpty()) {
                controller().setText("u-name", uName);
            }
        }
    }

    private void browseAccInfo(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        long id = tableModel.getNumberByName(selectedRow, "auId");
        EditPerAccountInfoDlg dlg = new EditPerAccountInfoDlg(id, 0);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        String uName = controller().getText("u-name");
        switch (uName) {
            case "我自己":
                uName = "+";
                break;
            case "全部":
                uName = "*";
                break;
        }
        params.put("u-name", uName);
        params.put("gender", controller().getNumber("gender"));
        params.put("user-type", controller().getText("user-type"));
        params.put("jx-status", controller().getNumber("jx-status"));
        params.put("search-key", controller().getText("search-key"));
        controller().disable("search");
        new CrmProxy().myRegUsers(params)
                      .thenApplyAsync(Result::array)
                      .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle)
                      .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallback(
            Object[] obj
    ) {
        ids = obj;
        lists = new ArrayList<>();
        size = obj.length;
        searchCallback2(null);
    }

    private void searchCallback2(
            ActionEvent actionEvent
    ) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

            @Override
            public String getTitle() {
                return controller().getMessage("search-title");
            }

            @Override
            protected Collection<Map<String, Object>> load() {
                List<Map<String, Object>> retList = new ArrayList<>();
                Date datePoint = controller().getDate("datepoint");
                for (int i = 0; i < ids.length; i++) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("datepoint", datePoint);
                    params.put("au-id", ids[i]);
                    retList.add(params);
                }
                return retList;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在执行";
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            ) {
                setIndex(i);
                new CrmProxy().myRegUsersById(params)
                              .thenApplyAsync(Result::map)
                              .thenAcceptAsync(this::searchCallback2, UPDATE_UI)
                              .exceptionally(ErrorHandler::handle);
            }

            private void searchCallback2(
                    Map<String, Object> map
            ) {
                lists.add(map);
                final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
                if (getIndex() == size - 1) {
                    BigDecimal total1 = new BigDecimal(0);
                    BigDecimal total2 = new BigDecimal(0);
                    BigDecimal total3 = new BigDecimal(0);
                    BigDecimal total4 = new BigDecimal(0);
                    BigDecimal total5 = new BigDecimal(0);
                    BigDecimal total6 = new BigDecimal(0);
                    BigDecimal total7 = new BigDecimal(0);
                    tableModel.setAllRows(lists);
                    final Map<String, Object> params = new HashMap<>();
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        BigDecimal amt1 = tableModel.getBigDecimalByName(i, "todayInvestAmt");
                        total1 = total1.add(amt1);
                        BigDecimal amt2 = tableModel.getBigDecimalByName(i, "todayWithdrawAmt");
                        total2 = total2.add(amt2);
                        BigDecimal amt3 = tableModel.getBigDecimalByName(i, "todayRechargeAmt");
                        total3 = total3.add(amt3);
                        BigDecimal amt4 = tableModel.getBigDecimalByName(i, "todayRepayCapitalAmt");
                        total4 = total4.add(amt4);
                        BigDecimal amt5 = tableModel.getBigDecimalByName(i, "investRemainAmt");
                        total5 = total5.add(amt5);
                        BigDecimal amt6 = tableModel.getBigDecimalByName(i, "investCount");
                        total6 = total6.add(amt6);
                        BigDecimal amt7 = tableModel.getBigDecimalByName(i, "sumInvestAmt");
                        total7 = total7.add(amt7);
                    }
                    params.put("loginName", "<总计>");
                    params.put("todayInvestAmt", total1);
                    params.put("todayWithdrawAmt", total2);
                    params.put("todayRechargeAmt", total3);
                    params.put("todayRepayCapitalAmt", total4);
                    params.put("investRemainAmt", total5);
                    params.put("investCount", total6);
                    params.put("sumInvestAmt", total7);
                    tableModel.addRow(params);
                }
            }
        });
        showModel(null, dlg);
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("acc-info");
        } else if (selectedRows.length > 1) {
            controller().disable("acc-info");
        } else {
            controller().disable("acc-info");
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
