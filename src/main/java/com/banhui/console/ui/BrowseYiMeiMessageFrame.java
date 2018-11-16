package com.banhui.console.ui;

import com.banhui.console.rpc.MessageProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.UIUtils.ceilingOfDay;
import static org.xx.armory.swing.UIUtils.floorOfDay;

public class BrowseYiMeiMessageFrame
        extends InternalFramePane {

    private final Logger logger = LoggerFactory.getLogger(BrowseYiMeiMessageFrame.class);

    /**
     * {@inheritDoc}
     */
    public BrowseYiMeiMessageFrame() {
        controller().connect("search", this::search);
        controller().connect("accelerate-date","change",this::accelerateDate);
    }

    private void search(
            ActionEvent event
    ) {
        assertUIThread();

        final String key = controller().getText("search-key");
        final String mobile = controller().getText("mobile");
        final Date startDate = floorOfDay(controller().getDate("start-date"));
        final Date endDate = ceilingOfDay(controller().getDate("end-date"));

        final Map<String, Object> params = new HashMap<>();
        params.put("start-time", startDate);
        params.put("end-time", endDate);
        params.put("search-key", key);
        params.put("mobile", mobile);
        params.put("count", longValue("500"));


        controller().disable("search");
//串行异步，链式异步
        new MessageProxy().queryYmMsgs(params)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback,UPDATE_UI);
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
    private void searchCallback(
            List<Map<String,Object>> maps,
            Throwable t
    ) {
        if(t!=null){
            ErrorHandler.handle(t);
        }else{
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(maps);
        }
        controller().enable("search");
    }
}
