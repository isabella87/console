package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectRepayProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.listValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;


public class EditBonusDetailRepaysDlg extends DialogPane {

    private volatile long pId;
    private volatile long pbdId;
    private volatile long tranType;
    private volatile long uploadStatus;

    private BigDecimal executedAmt = BigDecimal.ZERO;
    private BigDecimal sumAmt = BigDecimal.ZERO;

    private List<Map<String, Object>> children;

    public EditBonusDetailRepaysDlg(
            long pId,
            long pbdId,
            long tranType,
            long uploadStatus
    ) {
        this.pId = pId;
        this.pbdId = pbdId;
        this.tranType = tranType;
        this.uploadStatus = uploadStatus;

        setTitle(getTitle() + "-" + pbdId);

        controller().connect("list", "change", this::listChanged);
        controller().connect("execute", this::execute);
        controller().connect("refresh", this::refresh);
        controller().connect("export", this::export);

        controller().disable("execute");

        controller().call("refresh");
    }

    private void export(ActionEvent actionEvent) {
        controller().disable("export");

        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        new ExcelExportUtil(getTitle(), tableModel).choiceDirToSave();

        controller().enable("export");

    }

    //执行还款
    private void execute(
            ActionEvent actionEvent
    ) {

        String confirmRepayText = controller().formatMessage("confirm-repay");
        String confirmText = controller().formatMessage("confirm-text");
        if (confirm(this.getOwner(), confirmRepayText, confirmText)) {

            controller().disable("execute");

            Map<String, Object> params = new HashMap<>();
            params.put("pId", pId);
            params.put("pbdId", pbdId);
            params.put("tran-type", tranType);

            new ProjectRepayProxy().executeRepay(params)
                                   .thenApplyAsync(Result::map)
                                   .whenCompleteAsync(this::executeCallback, UPDATE_UI);

        }
    }

    private void executeCallback(
            Map<String, Object> map,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().call("refresh");
        }
    }

    private void refresh(
            ActionEvent actionEvent
    ) {
        controller().disable("refresh");

        Map<String, Object> params = new HashMap<>();
        params.put("pId", pId);
        params.put("pbdId", pbdId);
        new ProjectRepayProxy().queryBonusDetailRepays(params)
                               .thenApplyAsync(Result::map)
                               .whenCompleteAsync(this::refreshCallback, UPDATE_UI);

    }

    private void refreshCallback(
            Map<String, Object> map,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            JTable detailTable = controller().get(JTable.class, "detail");
            final TypedTableModel detailTableModel = (TypedTableModel) detailTable.getModel();
            detailTableModel.setAllRows(Collections.emptyList());

            final List<Map<String, Object>> items = listValue(map, "items");
            children = listValue(map, "children");

            JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.setAllRows(items);
            boolean haveUnRepayed = false;

            if (!items.isEmpty()) {
                sumAmt = BigDecimal.ZERO;
                executedAmt = BigDecimal.ZERO;

                for (Map<String, Object> mapItem : items) {
                    long status = longValue(mapItem, "status");
                    BigDecimal amt = decimalValue(mapItem, "amt");

                    if (status == 1) {
                        executedAmt = executedAmt.add(amt);
                    } else {
                        haveUnRepayed = true;
                    }
                    sumAmt = sumAmt.add(amt);
                }
            }
            if (haveUnRepayed && uploadStatus != -1) {
                controller().enable("execute");
            }
            displayStatisticData();
        }
        controller().enable("refresh");


    }

    private void displayStatisticData() {

        JLabel statisticLable = controller().get(JLabel.class, "statistic");
        statisticLable.setForeground(Color.blue);
        controller().setText("statistic", controller().formatMessage("statistic-text", executedAmt, sumAmt));
    }


    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();

        if (selectedRows.length == 1) {

            JTable table = controller().get(JTable.class, "list");
            TypedTableModel tableModel = (TypedTableModel) table.getModel();
            int selectedRow = table.getSelectedRow();
            long trId = tableModel.getNumberByName(selectedRow, "trId");

            final TypedTableModel detailTableModel = (TypedTableModel) controller().get(JTable.class, "detail").getModel();
            Collection<Map<String, Object>> child = new ArrayList<>();
            for (Map<String, Object> item : children) {
                if (longValue(item, "tsId") == trId) {
                    child.add(item);
                }
            }
            detailTableModel.setAllRows(child);
        }
    }
}
