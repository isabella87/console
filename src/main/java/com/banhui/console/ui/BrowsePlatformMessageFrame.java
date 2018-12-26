package com.banhui.console.ui;

import com.banhui.console.rpc.MessageProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowsePlatformMessageFrame
        extends InternalFramePane {

    private final Logger logger = LoggerFactory.getLogger(BrowsePlatformMessageFrame.class);

    /**
     * {@inheritDoc}
     */
    public BrowsePlatformMessageFrame() {
        controller().connect("search", this::search);
        controller().connect("create", this::create);
        controller().connect("edit", this::edit);
        controller().connect("accelerate-date", "change", this::accelerateDate);
        controller().connect("list", "change", this::listChanged);

        controller().disable("edit");

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        MainFrame.setTableTitleAndTableModel(getTitle(),tableModel);
    }

    private void accelerateDate(
            Object event
    ) {
        final int years = controller().getInteger("accelerate-date");
        if (years >= 0) {
            DateRange dateRange = latestSomeYears(new Date(), years);
            if (dateRange != null) {
                controller().setDate("start-date", dateRange.getStart());
                controller().setDate("end-date", dateRange.getEnd());
            }
        }
    }
    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final String key = controller().getText("search-key");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
        params.put("start-time", startDate);
        params.put("end-time", endDate);
        params.put("search-key", key);


        controller().disable("search");
//串行异步，链式异步
        new MessageProxy().queryMsgListByCond(params)
                          .thenApplyAsync(Result::list)
                         .whenCompleteAsync(this::searchCallback,UPDATE_UI);
    }

    private void searchCallback(
            List<Map<String,Object>> maps,
            Throwable t
    ) {
        if(t!= null){
            ErrorHandler.handle(t);
        }else{
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(maps);
        }
        controller().enable("search");
    }

    private void create(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;

        final EditPlatformMessageDlg dlg = new EditPlatformMessageDlg(0);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
    }

    private void edit(
            ActionEvent event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        final long id = tableModel.getNumberByName(selectedRow, "amId");
        final EditPlatformMessageDlg dlg = new EditPlatformMessageDlg(id);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();

            if (row != null && !row.isEmpty()) {
                tableModel.setRow(selectedRow, row);
            }
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            // 选中了行，并且仅选中一行。
            controller().enable("edit");
        } else if (selectedRows.length > 1) {
            controller().disable("edit");
        }
    }
}
