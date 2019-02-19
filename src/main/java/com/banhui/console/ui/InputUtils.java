package com.banhui.console.ui;

import org.xx.armory.commons.DateRange;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    private static Map<Integer, Integer> monthLastDay = new HashMap<>();

    static {
        monthLastDay.put(0, 31);
        monthLastDay.put(1, 28);
        monthLastDay.put(2, 31);
        monthLastDay.put(3, 30);
        monthLastDay.put(4, 31);
        monthLastDay.put(5, 30);
        monthLastDay.put(6, 31);
        monthLastDay.put(7, 31);
        monthLastDay.put(8, 30);
        monthLastDay.put(9, 31);
        monthLastDay.put(10, 30);
        monthLastDay.put(11, 31);
    }

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

    /**
     * 返回当前日期的前N周日期，以周一到周天为一个自然周。例如当前2019年1月23日，当前日期的前2周日期的始末分别是2019年1月7号，和2019年1月20号
     * 注意：为了界面展示效果，对于结束日期没有写成ceilingOfDay形式，但在外围传入后台的时候要取出该值用ceilingOfDay函数包装。
     *
     * @param date
     * @param n
     * @return
     */
    public static DateRange latestNweeks(
            Date date,
            int n
    ) {
        Date d1;
        Date d2;
        Calendar curCal = Calendar.getInstance();
        curCal.setTime(date);
        System.out.println("cur Day of week:" + curCal.get(Calendar.DAY_OF_WEEK));
        if (n == 0) {
            curCal.add(Calendar.DAY_OF_YEAR, -(curCal.get(Calendar.DAY_OF_WEEK) - 2));
            d1 = curCal.getTime();
            curCal.add(Calendar.DAY_OF_YEAR, (7 - (curCal.get(Calendar.DAY_OF_WEEK)) + 1));
            d2 = curCal.getTime();
        } else if (n > 0) {
            curCal.add(Calendar.DAY_OF_YEAR, -(curCal.get(Calendar.DAY_OF_WEEK) - 1));
            d2 = curCal.getTime();
            curCal.add(Calendar.DAY_OF_YEAR, -(n * 7 - 1));
            d1 = curCal.getTime();
        } else {
            curCal.add(Calendar.DAY_OF_YEAR, (7 - (curCal.get(Calendar.DAY_OF_WEEK)) + 2));
            d1 = curCal.getTime();
            curCal.add(Calendar.DAY_OF_YEAR, -n * 7 + 1 - curCal.get(Calendar.DAY_OF_WEEK));
            d2 = curCal.getTime();
        }
        return new DateRange(floorOfDay(d1), floorOfDay(d2));

    }

    /**
     * 返回当前日期的前N月日期的1号，以自然月计算。例如当前2019年1月23日，当前日期的前2月日期为2018年11月1日
     *
     * @param date
     * @param n
     * @return
     */
    private static Date getFirstDateOfReduceLatestNmonth(
            Date date,
            int n
    ) {
        Calendar curCal = Calendar.getInstance();
        curCal.setTime(date);
        int curMonth = curCal.get(Calendar.MONTH);
        int reduceN;
        int redYear = 0;
        int monthVal1 = 0;

        if (curMonth >= n) {
            reduceN = 0;
            monthVal1 = curMonth - n;
        } else {
            reduceN = n - curMonth;
        }
        while (reduceN > 0) {
            redYear++;
            if (12 >= reduceN) {
                monthVal1 = 12 - reduceN;
                reduceN = 0;
            } else {
                reduceN = reduceN - 12;
            }
        }
        curCal.add(Calendar.YEAR, -redYear);
        curCal.set(Calendar.MONTH, monthVal1);
        curCal.set(Calendar.DAY_OF_MONTH, 1);

        return curCal.getTime();

    }

    private static Date getFirstDateOfAddLatestNmonth(
            Date date,
            int n
    ) {
        Calendar curCalendar = Calendar.getInstance();
        curCalendar.setTime(date);
        int curMonth = curCalendar.get(Calendar.MONTH);

        int addMonthNum = 0;
        int addYearNum = 0;
        int monthVal = 0;
        if ((11 - curMonth) >= -n) {
            addMonthNum = -n;
            monthVal = curMonth - n;
        } else {
            addMonthNum = 11 - curMonth;
        }
        while (addMonthNum < -n) {
            addYearNum++;
            if ((-n - addMonthNum) <= 12) {
                monthVal = (-n - addMonthNum) - 1;
                addMonthNum = -n;
            } else {
                addMonthNum += 12;
            }
        }
        curCalendar.add(Calendar.YEAR, addYearNum);
        curCalendar.set(Calendar.MONTH, monthVal);
        curCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return curCalendar.getTime();
    }

    /**
     * 返回当前日期的前N月日期，以自然月计算。例如当前2019年1月23日，当前日期的前2月日期的始末分别是2018年11月1号，和2018年12月31号
     * 注意：为了界面展示效果，对于结束日期没有写成ceilingOfDay形式，但在外围传入后台的时候要取出该值用ceilingOfDay函数包装。
     *
     * @param date
     * @param n
     * @return
     */
    public static DateRange latestNmonths(
            Date date,
            int n
    ) {
        Date startDate = null;
        Date endDate = null;
        Calendar endCalendar;
        if (n >= 0) {
            startDate = getFirstDateOfReduceLatestNmonth(date, n);
            endCalendar = Calendar.getInstance();
            endCalendar.setTime(n == 0 ? date : getFirstDateOfReduceLatestNmonth(date, 1));
            endCalendar.set(Calendar.DAY_OF_MONTH, monthLastDay.get(endCalendar.get(Calendar.MONTH)));
            endDate = endCalendar.getTime();
        } else {  //n < 0

            startDate = getFirstDateOfAddLatestNmonth(date, -1);

            endCalendar = Calendar.getInstance();
            endCalendar.setTime(getFirstDateOfAddLatestNmonth(date, n));
            endCalendar.set(Calendar.DAY_OF_MONTH, monthLastDay.get(endCalendar.get(Calendar.MONTH)));
            endDate = endCalendar.getTime();
        }

        return new DateRange(floorOfDay(startDate), floorOfDay(endDate));

    }

    public static DateRange latestNyears(
            Date date,
            int n
    ) {
        Date startDate = null;
        Date endDate = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (n == 0) {
            int yearNum = calendar.get(Calendar.YEAR);
            calendar.set(yearNum, 0, 1);
            startDate = calendar.getTime();
            calendar.set(yearNum, 11, 31);
            endDate = calendar.getTime();
        } else if (n > 0) {
            int yearNum = calendar.get(Calendar.YEAR);
            calendar.set(yearNum - 1, 11, 31);
            endDate = calendar.getTime();

            calendar.set(yearNum - 1 * n, 0, 1);
            startDate = calendar.getTime();
        } else {     // n < 0
            int yearNum = calendar.get(Calendar.YEAR);
            calendar.set(yearNum + 1, 0, 1);
            startDate = calendar.getTime();

            calendar.set(yearNum - n, 11, 31);
            endDate = calendar.getTime();
        }

        return new DateRange(floorOfDay(startDate), floorOfDay(endDate));

    }

    public static DateRange latestNdays(
            Date date,
            int n
    ) {
        notNull(date, "day");

        Date firstDay;
        Date endDate;
        if (n == 0) {
            firstDay = endDate = date;
        } else if (n > 0) {
            firstDay = addDays(date, -n);
            endDate = addDays(date, -1);
        } else {
            firstDay = addDays(date, 1);
            endDate = addDays(date, -n);
        }

        return truncateRange(firstDay, endDate);
    }

    public static void main(String[] args) {
        Calendar cur = Calendar.getInstance();
        cur.add(Calendar.DAY_OF_YEAR, 1);
        /*System.out.println("curDate:" + cur.getTime());
        DateRange dateRange = lastedNweeks(cur.getTime(), -3);
        System.out.println("weeks:" + 0);
        System.out.println("start: " + dateRange.getStart());
        System.out.println("end:   " + dateRange.getEnd());
        System.out.println("end:   " + ceilingOfDay(dateRange.getEnd()));*/

        DateRange dateRange = latestNweeks(cur.getTime(), 0);
        System.out.println("start: " + dateRange.getStart());
        System.out.println("end:   " + dateRange.getEnd());

    }
}
