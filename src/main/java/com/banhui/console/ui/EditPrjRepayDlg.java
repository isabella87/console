package com.banhui.console.ui;


import com.banhui.console.rpc.ProjectRepayProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditPrjRepayDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditPrjRepayDlg.class);

    private volatile long pId;
    private volatile long status;

    public EditPrjRepayDlg(
            long pId,
            long status
    ) {
        this.pId = pId;
        this.status = status;

        setTitle(getTitle() + pId);

        controller().connect("create", this::create);
        controller().connect("forbid", this::forbid);
        controller().connect("refresh", this::refresh);
        controller().connect("search", this::search);
        controller().connect("export", this::export);
        controller().connect("list", "change", this::listChange);
        controller().connect("detail", "change", this::detailListChange);

        controller().call("refresh");

        controller().disable("create");
        controller().disable("forbid");
        controller().disable("search");

    }

    private void export(ActionEvent actionEvent) {
        controller().disable("export");

        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        new ExcelUtil(getTitle(), tableModel).choiceDirToSave();

        controller().enable("export");
    }

    private void forbid(ActionEvent actionEvent) {
        JTable table = controller().get(JTable.class, "detail");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        final int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 1) {
            final long pbdId = tableModel.getNumberByName(selectedRows[0], "pbdId");

            if (confirm(this.getOwner(), controller().formatMessage("confirm-delete", pbdId))) {
                Map<String, Object> params = new HashMap<>();
                params.put("pId", pId);
                params.put("pbdId", pbdId);
                new ProjectRepayProxy().delPrjBonusDetail(params)
                                       .thenApplyAsync(Result::longValue)
                                       .whenCompleteAsync(this::forbidCallback, UPDATE_UI);
            }

        }


    }

    private void forbidCallback(
            Long optionalLong,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            new ProjectRepayProxy().queryPrjBonusDetail(pId)
                                   .thenApplyAsync(Result::list)
                                   .whenCompleteAsync(this::queryBonusDetailCallback, UPDATE_UI);
        }
    }

    private void queryBonusDetailCallback(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            JTable table = controller().get(JTable.class, "detail");
            TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.setAllRows(maps);
        }
    }

    private void detailListChange(Object actionEvent) {
        JTable table = controller().get(JTable.class, "detail");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        final int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 1) {
            controller().enable("search");

            final long uploadStatus = tableModel.getNumberByName(selectedRows[0], "uploadStatus");
            if (uploadStatus != -1) {
                controller().enable("forbid");
            } else {
                controller().disable("forbid");
            }

        } else {
            controller().disable("search");
            controller().disable("forbid");
        }
    }

    private void search(ActionEvent actionEvent) {

        JTable table = controller().get(JTable.class, "detail");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        long pbdId = tableModel.getNumberByName(selectedRow, "pbdId");
        long tranType = tableModel.getNumberByName(selectedRow, "tranType");
        final long uploadStatus = tableModel.getNumberByName(selectedRow, "uploadStatus");

        EditBonusDetailRepaysDlg dlg = new EditBonusDetailRepaysDlg(pbdId, pbdId, tranType, uploadStatus);
        dlg.setFixedSize(false);

        showModel(null, dlg);
    }

    private void listChange(Object actionEvent) {
        JTable table = controller().get(JTable.class, "list");

        final int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 1) {
            if (status == 90) {
                controller().enable("create");
            } else {
                controller().disable("create");
            }

        } else {
            controller().disable("create");
        }

    }

    private void refresh(
            ActionEvent event
    ) {
        allDone(new ProjectRepayProxy().queryPrjBonus(pId),
                new ProjectRepayProxy().queryPrjBonusDetail(pId)
        ).thenApply(results -> new Object[]{
                results[0].list(),
                results[1].list()
        }).whenCompleteAsync(this::refreshCallback, UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void refreshCallback(
            Object[] results,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            JTable table1 = controller().get(JTable.class, "list");
            TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
            tableModel1.setAllRows((Collection<Map<String, Object>>) results[0]);

            JTable table2 = controller().get(JTable.class, "detail");
            TypedTableModel tableModel2 = (TypedTableModel) table2.getModel();
            tableModel2.setAllRows((Collection<Map<String, Object>>) results[1]);
        }

    }

    private void create(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();

        final BigDecimal unpaidAmt = tableModel.getBigDecimalByName(selectedRow, "unpaidAmt");
        final Date dueTime = tableModel.getDateByName(selectedRow, "dueTime");
        final long tranType = tableModel.getNumberByName(selectedRow, "tranType");
        final long tranNo = tableModel.getNumberByName(selectedRow, "tranNo");

        CreatePrjRepayDlg dlg = new CreatePrjRepayDlg(pId, unpaidAmt, dueTime, tranType, tranNo);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                JTable detailTable = controller().get(JTable.class, "detail");
                TypedTableModel detailTableModel = (TypedTableModel) detailTable.getModel();
                detailTableModel.insertRow(0, dlg.getResultRow());
            }

        }
    }

    public final long getId() {
        return this.pId;
    }

}
