package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.ui.InputUtils.lastMonth;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseQueryNewRegClientFrame
        extends BaseFramePane {

    private final Logger logger = LoggerFactory.getLogger(BrowseQueryNewRegClientFrame.class);

    /**
     * {@inheritDoc}
     */
    public BrowseQueryNewRegClientFrame() {
        controller().connect("search", this::search);

        controller().setDate("start-date", lastMonth(new Date()));

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        setTableTitleAndTableModelForExport(getTitle(), tableModel);
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final Date startDate = floorOfDay(controller().getDate("start-date"));

        final Map<String, Object> params = new HashMap<>();
        params.put("datepoint", startDate);


        controller().disable("search");

        new CrmProxy().queryNewRegs(params)
                      .thenApplyAsync(Result::list)
                      .whenCompleteAsync(this::searchCallback, UPDATE_UI)
                      .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
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
}
