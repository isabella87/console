package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;


public class BrowsePrjTenderDlg extends DialogPane {

    private volatile long id;
    private volatile int role;
    private Map<String, Object> row;

    public BrowsePrjTenderDlg(
            long id,
            int role,
            boolean lockStatus
    ) {
        controller().disable("revoke");
        controller().connect("list", "change", this::listChanged);

        controller().connect("revoke", this::revoke);
        controller().connect("refresh", this::refresh);
        controller().connect("midway", this::midway);
        controller().connect("check", this::check);
        controller().connect("excel", this::excel);

        this.id = id;
        this.role = role;
        setTitle(getTitle() + id);
        controller().call("refresh");
        if (lockStatus) {
            controller().hide("midway");
            controller().hide("revoke");
            controller().hide("check");
        }
        if (role == 60 || role == 70 || role == 80 || role == 90 || role == 999) {
            controller().hide("midway");
        }
        if (role == -1) {
            controller().hide("midway");
            controller().hide("check");
        }

    }

    private void check(ActionEvent actionEvent) {
        final BrowseCheckTenderDlg dlg = new BrowseCheckTenderDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void excel(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        new ExcelExportUtil(getTitle(), tableModel).choiceDirToSave(getTitle());
    }

    //中途流标
    private void midway(
            ActionEvent actionEvent
    ) {
        String confirmTenderText = controller().formatMessage("confirm-tender-text");
        String confirmComplete = controller().formatMessage("confirm-complete");
        if (confirm(this.getOwner(), confirmTenderText, confirmComplete)) {
            final Map<String, Object> params = new HashMap<>();
            if (this.id != 0) {
                params.put("p-id", id);
            }
            params.put("comments", null);
            new AuditProxy().busVpStopRaising(params)
                            .thenApplyAsync(Result::map)
                            .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                            .thenAcceptAsync(v -> controller().enable("auctions"), UPDATE_UI);
        }
    }


    //撤销投标
    private void revoke(
            ActionEvent actionEvent
    ) {
        controller().disable("revoke");
        String confirmRevokeText = controller().formatMessage("confirm-revoke-text");
        if (confirm(this.getOwner(), confirmRevokeText)) {
            final Map<String, Object> params = new HashMap<>();
            params.put("remark", null);
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
            final long ttId = tableModel.getNumberByName(selectedRow1, "ttId");
            params.put("tt-id", ttId);
            new AuditProxy().createTsCancelTender(params)
                            .thenApplyAsync(Result::map)
                            .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }


    //刷新
    private void refresh(
            ActionEvent actionEvent
    ) {
        controller().disable("revoke");
        new AuditProxy().queryTenders(id)
                        .thenApplyAsync(Result::list)
                        .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length >= 1) {
            controller().enable("revoke");
        }
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

    private void delCallback(
            Map<String, Object> deletedRow,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(deletedRow.get("ttId"), row.get("ttId")));
        }
    }

    private void saveCallback(
            Map<String, Object> row,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.row = row;
            super.done(OK);
        }
    }
}
