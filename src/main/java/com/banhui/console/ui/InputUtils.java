package com.banhui.console.ui;

import org.apache.commons.lang3.time.DateUtils;
import org.xx.armory.commons.DateRange;

import java.time.Year;
import java.util.Calendar;
import java.util.Date;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMonths;
import static org.apache.commons.lang3.time.DateUtils.addWeeks;
import static org.apache.commons.lang3.time.DateUtils.addYears;
import static org.xx.armory.commons.Validators.greaterThanOrEqual;

/**
 * 和输入相关的工具类。
 */
public final class InputUtils {
    private InputUtils() {
    }

    public static DateRange latestSomeDays(
            int days
    ) {
        greaterThanOrEqual(days, "days", 1);

        final Date today = new Date();
        final Date firstDay = addDays(today, -days);

        return new DateRange(firstDay, today);
    }

    public static DateRange latestSomeWeeks(
            int weeks
    ) {
        greaterThanOrEqual(weeks, "weeks", 1);

        final Date today = new Date();
        final Date firstDay = addWeeks(today, -weeks);

        return new DateRange(firstDay, today);
    }

    public static DateRange latestSomeMonths(
            int months
    ) {
        greaterThanOrEqual(months, "months", 1);

        final Date today = new Date();
        final Date firstDay = addMonths(today, -months);

        return new DateRange(firstDay, today);
    }

    public static DateRange latestSomeYears(
            int years
    ) {
        if (years < 0) {
            return null;
        }

        if (years == 0) {
            return thisYear();
        }

        final Date today = new Date();
        final Date firstDay = addYears(today, -years);

        return new DateRange(firstDay, today);
    }

    public static DateRange thisYear(

    ){
        final Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year,0,1);

        return new DateRange(calendar.getTime(), today);
    }

    public static void main(String[] args){
        DateRange dateRange =  thisYear();
        System.out.println(dateRange.getStart());
        System.out.println(dateRange.getEnd());
    }
}
