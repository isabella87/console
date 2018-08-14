package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.SysProxy;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseMyClientFrame
        extends InternalFramePane {

    private volatile List<Map<String, Object>> list;

    public BrowseMyClientFrame() {
        controller().readOnly("u-name", true);
        controller().setText("u-name", "全部");
        controller().disable("acc-info");

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
        controller().disable("search    ");
        new CrmProxy().myRegUsers(params)
                      .thenApplyAsync(Result::array)
                      .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle)
                      .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallback(
            Object[] c
    ) {
        list = new ArrayList<>();
        Date datePoint = controller().getDate("datepoint");
        for (Object auId : c) {
            final Map<String, Object> params = new HashMap<>();
            params.put("datepoint", datePoint);
            params.put("au-id", auId);
            new CrmProxy().myRegUsersById(params)
                          .thenApplyAsync(Result::map)
                          .thenAcceptAsync(this::searchCallback2, UPDATE_UI)
                          .exceptionally(ErrorHandler::handle);
        }
    }

    private void searchCallback2(
            Map<String, Object> map
    ) {
        list.add(map);
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.addRow(map);
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
}
