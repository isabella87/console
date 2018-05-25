package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseProjectsFrame
        extends InternalFramePane {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initUi() {
        super.initUi();

        controller().connect("search", this::search);
        controller().connect("view", this::view);
        controller().connect("edit", this::edit);
        controller().connect("list", "change", this::listChanged);

        controller().disable("view");
        controller().disable("edit");
        controller().disable("delete");
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final int dateType = controller().getInteger("date-type");
        final int status = controller().getInteger("status");
        final int keyType = controller().getInteger("key-type");
        final String key = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
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
        }

        if (status != Integer.MAX_VALUE) {
            params.put("status", status);
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
                          .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                          .exceptionally(MsgBox::showError)
                          .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);
    }

    private void view(
            ActionEvent event
    ) {

    }

    private void edit(
            ActionEvent event
    ) {

    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            // 选中了行，并且仅选中一行。
            controller().enable("view");
            controller().enable("edit");
            controller().enable("delete");
        } else if (selectedRows.length > 1) {
            controller().disable("view");
            controller().disable("edit");
            controller().enable("delete");
        } else {
            controller().disable("view");
            controller().disable("edit");
            controller().disable("delete");
        }
    }
}
