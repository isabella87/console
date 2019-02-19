package com.banhui.console.ui;

import com.banhui.console.rpc.DisclosureProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class BrowseInfoDisclosureFrame
        extends BaseFramePane {
    /**
     * {@inheritDoc}
     */
    public BrowseInfoDisclosureFrame() {

        controller().connect("search", this::search);
        controller().connect("edit", this::edit);
        controller().connect("create", this::create);
        controller().connect("delete", this::delete);
        controller().connect("detail", this::detail);
        controller().connect("createDetail", this::createDetail);

        controller().connect("list", "change", this::listChanged);
        controller().disable("edit");
        controller().disable("delete");
        controller().disable("detail");
        controller().disable("createDetail");
    }

    private void createDetail(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long adrId = tableModel.getNumberByName(selectedRow1, "adrId");
        final EditDisclosureDetailDlg dlg = new EditDisclosureDetailDlg(adrId, 0);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            controller().call("search");
        }
    }

    private void detail(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long adrId = tableModel.getNumberByName(selectedRow1, "adrId");
        final EditDisclosureDetailDlg dlg = new EditDisclosureDetailDlg(adrId, 1);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void delete(
            ActionEvent actionEvent
    ) {
        String confirmDeleteText = controller().formatMessage("confirm-delete-text");
        if (confirm(null, confirmDeleteText)) {
            controller().disable("delete");
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
            final long adrId = tableModel.getNumberByName(selectedRow1, "adrId");
            final Map<String, Object> params = new HashMap<>();
            params.put("adr-id", adrId);
            new DisclosureProxy().deleteDisclosure(params)
                                 .thenApplyAsync(Result::booleanValue)
                                 .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            boolean flag,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else if (flag) {
            controller().call("search");
        }
    }

    private void create(
            ActionEvent actionEvent
    ) {
        final EditInfoDisclosureDlg dlg = new EditInfoDisclosureDlg(0);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            controller().call("search");
        }
    }

    private void edit(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        final long adrId = tableModel.getNumberByName(selectedRow1, "adrId");
        final EditInfoDisclosureDlg dlg = new EditInfoDisclosureDlg(adrId);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            controller().call("search");
        }

    }


    private void search(
            ActionEvent event
    ) {
        assertUIThread();
        final Long year = controller().getNumber("year");
        final Long month = controller().getNumber("month");
        final long type = controller().getNumber("type");

        final Map<String, Object> params = new HashMap<>();
        params.put("year", year);
        params.put("month", month);
        params.put("type", type);
        controller().disable("search");

        new DisclosureProxy().queryList(params)
                             .thenApplyAsync(Result::list)
                             .whenCompleteAsync(this::searchCallBack, UPDATE_UI)
                             .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallBack(
            Collection<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(maps);
        }
    }


    private void listChanged(
            Object event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        int[] selectedRows = table.getSelectedRows();
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        if (selectedRows.length == 1) {
            // 选中了行，并且仅选中一行。
            final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
            boolean exist = tableModel.getBooleanByName(selectedRow1, "detailExist");
            if (exist) {
                controller().enable("detail");
                controller().disable("createDetail");
            } else {
                controller().disable("detail");
                controller().enable("createDetail");
            }
            controller().enable("create");
            controller().enable("edit");
            controller().enable("delete");
        } else {
            controller().enable("create");
            controller().disable("edit");
            controller().disable("delete");
            controller().disable("detail");
            controller().disable("createDetail");
        }
    }
}
