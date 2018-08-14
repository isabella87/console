package com.banhui.console.ui;

import org.xx.armory.commons.DateRange;

import java.util.Calendar;
import java.util.Date;

import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addMonths;
import static org.apache.commons.lang3.time.DateUtils.addWeeks;
import static org.apache.commons.lang3.time.DateUtils.addYears;
import static org.apache.commons.lang3.time.DateUtils.ceiling;
import static org.apache.commons.lang3.time.DateUtils.truncate;
import static org.xx.armory.commons.Validators.greaterThanOrEqual;
import static org.xx.armory.commons.Validators.notNull;
import static org.xx.armory.swing.UIUtils.floorOfDay;

/**
 * 和输入相关的工具类。
 */
public final class InputUtils {
    private static final int DAYS_OF_WEEK = 7;

    /**
     * 禁止构造工具类。
     */
    private InputUtils() {
    }

    /**
     * 将起始日期和终止日期都截断到该日的开头，并构造日期范围。
     *
     * @param start
     *         起始日期。
     * @param end
     *         终止日期。
     * @return 起始日期和终止日期表示的日期范围。
     * @see DateRange#DateRange(Date, Date)
     */
    private static DateRange truncateRange(
            Date start,
            Date end
    ) {
        if (start != null) {
            start = floorOfDay(start);
        }
        if (end != null) {
            end = floorOfDay(end);
        }

        return new DateRange(start, end);
    }

    /**
     * 获取从指定日之前几天到指定日的日期范围。
     *
     * @param day
     *         指定日。
     * @param days
     *         之前的天数。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}或者参数{@code days}小于0。
     */
    public static DateRange latestSomeDays(
            Date day,
            int days
    ) {
        notNull(day, "day");
        greaterThanOrEqual(days, "days", 0);

        final Date firstDay = addDays(day, -days);

        return truncateRange(firstDay, day);
    }

    /**
     * 获取从指定日之前几周到指定日的日期范围。
     *
     * @param day
     *         指定日。
     * @param weeks
     *         之前的周数。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}或者参数{@code weeks}小于0。
     */
    public static DateRange latestSomeWeeks(
            Date day,
            int weeks
    ) {
        notNull(day, "day");
        greaterThanOrEqual(weeks, "weeks", 0);

        final Date firstDay = addWeeks(day, -weeks);

        return truncateRange(firstDay, day);
    }

    /**
     * 获取从指定日之前几月到指定日的日期范围。
     *
     * @param day
     *         指定日。
     * @param months
     *         之前的月数。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}或者参数{@code months}小于0。
     */
    public static DateRange latestSomeMonths(
            Date day,
            int months
    ) {
        notNull(day, "day");
        greaterThanOrEqual(months, "months", 0);

        final Date firstDay = addMonths(day, -months);

        return truncateRange(firstDay, day);
    }

    /**
     * 获取从指定日之前几年到指定日的日期范围。
     *
     * @param day
     *         指定日。
     * @param years
     *         之前的年数。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}或者参数{@code years}小于0。
     */
    public static DateRange latestSomeYears(
            Date day,
            int years
    ) {
        notNull(day, "day");
        greaterThanOrEqual(years, "years", 0);

        if (years == 0) {
            return thisYear(day);
        }

        final Date firstDay = addYears(day, -years);

        return truncateRange(firstDay, day);
    }

    /**
     * 获取从指定日所在年的日期范围。
     *
     * @param day
     *         指定日。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}。
     */
    public static DateRange thisYear(
            Date day
    ) {
        notNull(day, "day");

        final Date firstDay = truncate(day, Calendar.YEAR);
        final Date lastDay = addDays(ceiling(day, Calendar.YEAR), -1);

        return new DateRange(firstDay, lastDay);
    }

    /**
     * 获取从指定日所在周的日期范围，将周一作为每周的起始日。
     *
     * @param day
     *         指定日。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}。
     */
    public static DateRange thisWeek(
            Date day
    ) {
        return thisWeek(day, Calendar.MONDAY);
    }

    /**
     * 获取从指定日所在周的日期范围。
     *
     * @param day
     *         指定日。
     * @param firstDayOfWeek
     *         每周的起始日。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}。
     */
    public static DateRange thisWeek(
            Date day,
            int firstDayOfWeek
    ) {
        notNull(day, "day");
        if (firstDayOfWeek < Calendar.SUNDAY || firstDayOfWeek > Calendar.SATURDAY) {
            throw new IllegalArgumentException("firstDayOfWeek should between SUNDAY and SATURDAY");
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);

        int week = calendar.get(Calendar.DAY_OF_WEEK);
        week = (week - firstDayOfWeek + DAYS_OF_WEEK) % DAYS_OF_WEEK;

        final Date firstDay = addDays(truncate(day, Calendar.DATE), -week);
        final Date lastDay = addDays(firstDay, DAYS_OF_WEEK - 1);
        return new DateRange(firstDay, lastDay);
    }

    /**
     * 获取从指定日所在月的日期范围。
     *
     * @param day
     *         指定日。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}。
     */
    public static DateRange thisMonth(
            Date day
    ) {
        notNull(day, "day");

        final Date firstDay = truncate(day, Calendar.MONTH);
        final Date lastDay = addDays(ceiling(day, Calendar.MONTH), -1);

        return new DateRange(firstDay, lastDay);
    }

    /**
     * 输入时间的上个月第一天
     *
     * @param day
     * @return
     */
    public static Date lastMonth(Date day) {
        notNull(day, "day");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);

        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        if (month == 0) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
            calendar.set(Calendar.MONTH, 11);
        } else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        }
        return calendar.getTime();
    }

    /**
     * 获取从指定日当天的日期范围。
     *
     * @param day
     *         指定日。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}。
     */
    public static DateRange today(
            Date day
    ) {
        notNull(day, "day");

        return truncateRange(day, day);
    }

    /**
     * 获取从指定日第二天的日期范围。
     *
     * @param day
     *         指定日。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}。
     */
    public static DateRange tomorrow(
            Date day
    ) {
        notNull(day, "day");

        day = addDays(day, 1);

        return new DateRange(day, day);
    }

    /**
     * 获取从指定日前一天的日期范围。
     *
     * @param day
     *         指定日。
     * @return 日期范围。
     * @throws IllegalArgumentException
     *         如果参数{@code day}是{@code null}。
     */
    public static DateRange yesterday(
            Date day
    ) {
        notNull(day, "day");

        day = addDays(day, -1);

        return new DateRange(day, day);
    }
}
