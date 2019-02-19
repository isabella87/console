package com.banhui.console.ui;

import com.banhui.console.rpc.BaPrjBorOrgsProxy;
import com.banhui.console.rpc.BaPrjBorPersProxy;
import com.banhui.console.rpc.BaPrjGuaranteeOrgsProxy;
import com.banhui.console.rpc.BaPrjGuaranteePersProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class ChoosePrjGuaranteeBorDlg
        extends DialogPane {

    private volatile int role;
    private volatile long bgpId;
    private volatile long bgoId;
    private volatile long bpmpId;
    private volatile long bpmoId;
    private volatile String name;
    private volatile String showName;
    private String listName;

    public ChoosePrjGuaranteeBorDlg(
            int role
    ) {
        this.role = role;
        final Map<String, Object> params = new HashMap<>();

        controller().disable("ok");
        controller().connect("listGuaPer", "change", this::listChanged);
        controller().connect("listGuaOrg", "change", this::listChanged);
        controller().connect("listBorPer", "change", this::listChanged);
        controller().connect("listBorOrg", "change", this::listChanged);
        controller().connect("search", this::search);

        switch (role) {
            case 1:
                setTitle(controller().getMessage("guaPer"));
                controller().hide("listGuaOrg");
                controller().hide("listBorPer");
                controller().hide("listBorOrg");
                listName = "listGuaPer";
                new BaPrjGuaranteePersProxy().all(params)
                                             .thenApplyAsync(Result::list)
                                             .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
            case 2:
                setTitle(controller().getMessage("guaOrg"));
                controller().hide("listGuaPer");
                controller().hide("listBorPer");
                controller().hide("listBorOrg");
                listName = "listGuaOrg";
                new BaPrjGuaranteeOrgsProxy().all(params)
                                             .thenApplyAsync(Result::list)
                                             .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
            case 3:
                setTitle(controller().getMessage("borPer"));
                controller().hide("listGuaPer");
                controller().hide("listGuaOrg");
                controller().hide("listBorOrg");
                listName = "listBorPer";
                new BaPrjBorPersProxy().all(params)
                                       .thenApplyAsync(Result::list)
                                       .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
            case 4:
                setTitle(controller().getMessage("borOrg"));
                controller().hide("listGuaPer");
                controller().hide("listGuaOrg");
                controller().hide("listBorPer");
                listName = "listBorOrg";
                new BaPrjBorOrgsProxy().all(params)
                                       .thenApplyAsync(Result::list)
                                       .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
        }

    }

    private void searchCallback(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        JTable table1 = controller().get(JTable.class, listName);
        TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
        tableModel1.setAllRows(maps);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("key", controller().getText("key"));
        switch (role) {
            case 1:
                new BaPrjGuaranteePersProxy().all(params)
                                             .thenApplyAsync(Result::list)
                                             .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
            case 2:
                new BaPrjGuaranteeOrgsProxy().all(params)
                                             .thenApplyAsync(Result::list)
                                             .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
            case 3:
                new BaPrjBorPersProxy().all(params)
                                       .thenApplyAsync(Result::list)
                                       .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
            case 4:
                new BaPrjBorOrgsProxy().all(params)
                                       .thenApplyAsync(Result::list)
                                       .whenCompleteAsync(this::searchCallback, UPDATE_UI);
                break;
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");
            JTable table1 = controller().get(JTable.class, listName);
            TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
            final int selectedRow1 = table1.convertRowIndexToModel(table1.getSelectedRow());
            switch (role) {
                case 1:
                    this.bgpId = tableModel1.getNumberByName(selectedRow1, "bgpId");
                    this.showName = tableModel1.getStringByName(selectedRow1, "showName");
                    this.name = tableModel1.getStringByName(selectedRow1, "name");
                    break;
                case 2:
                    this.bgoId = tableModel1.getNumberByName(selectedRow1, "bgoId");
                    this.showName = tableModel1.getStringByName(selectedRow1, "showName");
                    this.name = tableModel1.getStringByName(selectedRow1, "name");
                    break;
                case 3:
                    this.bpmpId = tableModel1.getNumberByName(selectedRow1, "bpmpId");
                    this.name = tableModel1.getStringByName(selectedRow1, "realName");
                    break;
                case 4:
                    this.bpmoId = tableModel1.getNumberByName(selectedRow1, "bpmoId");
                    this.name = tableModel1.getStringByName(selectedRow1, "orgName");
                    break;
            }
            super.done(OK);
        } else {
            super.done(result);
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, listName).getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("ok");
        } else if (selectedRows.length > 1) {
            controller().disable("ok");
        } else {
            controller().disable("ok");
        }
    }

    public long getBgpId() {
        return bgpId;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getShowName() {
        return showName;
    }

    public long getBgoId() {
        return bgoId;
    }

    public long getBpmpId() {
        return bpmpId;
    }

    public long getBpmoId() {
        return bpmoId;
    }

}
