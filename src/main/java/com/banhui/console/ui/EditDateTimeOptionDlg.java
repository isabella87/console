package com.banhui.console.ui;


import static org.xx.armory.swing.DialogUtils.prompt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.commons.DateRange;
import org.xx.armory.swing.components.DialogPane;

import java.util.Date;


public class EditDateTimeOptionDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditDateTimeOptionDlg.class);


    private volatile DateRange dateRange;

    public EditDateTimeOptionDlg() {
        this.dateRange = null;
        controller().connect("dateNum", "change", this::dateNumChange);
        controller().connect("dateUnit", "change", this::dateUnitChange);

        controller().readOnly("startDate", true);
        controller().readOnly("endDate", true);
        controller().setInteger("dateNum",0);

    }

    private void dateUnitChange(Object o) {
        dateChangeEvent();
    }

    private void dateNumChange(Object o) {
        dateChangeEvent();
    }

    private void dateChangeEvent() {
        controller().setDate("startDate", null);
        controller().setDate("endDate", null);

        Integer dateNum = controller().getInteger("dateNum");
        System.out.println("***************************************************"+dateNum);
        Integer dateUnit = controller().getInteger("dateUnit");
        DateRange dateRange = null;
        Date now = new Date();
        if (dateNum != null && dateUnit != null) {
            //TODO
            switch (dateUnit) {
                case 0:
                    dateRange = InputUtils.latestNdays(now, dateNum);
                    break;
                case 1:
                    dateRange = InputUtils.latestNweeks(now, dateNum);
                    break;
                case 2:
                    dateRange = InputUtils.latestNmonths(now, dateNum);
                    break;
                case 3:
                    dateRange = InputUtils.latestNyears(now, dateNum);
            }
            if (dateRange != null) {
                controller().setDate("startDate", dateRange.getStart());
                controller().setDate("endDate", dateRange.getEnd());
            }
        } else {
            prompt(null, "请正确填写！");
        }

    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            Date startDate = controller().getDate("startDate");
            Date endDate = controller().getDate("endDate");
            if (startDate != null && endDate != null) {
                this.dateRange = new DateRange(startDate, endDate);
                super.done(OK);
            } else {
                prompt(null, "请选择时间阈");
            }

        } else {
            super.done(result);
        }
    }

    public DateRange getDateRange() {
        return this.dateRange;
    }
}
