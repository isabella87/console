package com.banhui.console.ui;

import org.junit.Test;
import org.xx.armory.commons.DateRange;

import java.util.Calendar;
import java.util.Date;

import static com.banhui.console.ui.InputUtils.latestSomeDays;
import static com.banhui.console.ui.InputUtils.latestSomeMonths;
import static com.banhui.console.ui.InputUtils.latestSomeWeeks;
import static com.banhui.console.ui.InputUtils.latestSomeYears;
import static com.banhui.console.ui.InputUtils.thisMonth;
import static com.banhui.console.ui.InputUtils.thisWeek;
import static com.banhui.console.ui.InputUtils.thisYear;
import static com.banhui.console.ui.InputUtils.today;
import static com.banhui.console.ui.InputUtils.tomorrow;
import static com.banhui.console.ui.InputUtils.yesterday;
import static org.junit.Assert.assertEquals;
import static org.xx.armory.commons.DateTimeUtils.createLocalDate;

public class TestInputUtils {
    @Test
    public void testLatestSomeDays() {
        final Date d1 = createLocalDate(1995, Calendar.AUGUST, 29, 18, 49, 56).getTime();
        final DateRange r1 = latestSomeDays(d1, 5);
        final DateRange r2 = latestSomeDays(d1, 33);

        assertEquals(new DateRange(
                createLocalDate(1995, Calendar.AUGUST, 24).getTime(),
                createLocalDate(1995, Calendar.AUGUST, 29).getTime()
        ), r1);

        assertEquals(new DateRange(
                createLocalDate(1995, Calendar.JULY, 27).getTime(),
                createLocalDate(1995, Calendar.AUGUST, 29).getTime()
        ), r2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomeDaysNullDay() {
        latestSomeDays(null, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomDaysNegativeDays() {
        final Date d1 = createLocalDate(1995, Calendar.AUGUST, 29, 18, 49, 56).getTime();
        latestSomeDays(d1, -1);
    }

    @Test
    public void testLatestSomeWeeks() {
        final Date d1 = createLocalDate(2018, Calendar.MAY, 31, 10, 10, 10).getTime();
        final DateRange r1 = latestSomeWeeks(d1, 4);
        final DateRange r2 = latestSomeWeeks(d1, 10);
        final DateRange r3 = latestSomeWeeks(d1, 23);
        final DateRange r4 = latestSomeWeeks(d1, 0);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.MAY, 3).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r1);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.MARCH, 22).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r2);
        assertEquals(new DateRange(
                createLocalDate(2017, Calendar.DECEMBER, 21).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r3);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.MAY, 31).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r4);

        final Date d2 = createLocalDate(2018, Calendar.MARCH, 7, 10, 10, 10).getTime();
        final DateRange r5 = latestSomeWeeks(d2, 1);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.FEBRUARY, 28).getTime(),
                createLocalDate(2018, Calendar.MARCH, 7).getTime()
        ), r5);
        final Date d3 = createLocalDate(2016, Calendar.MARCH, 7, 10, 10, 10).getTime();
        final DateRange r6 = latestSomeWeeks(d3, 1);
        assertEquals(new DateRange(
                createLocalDate(2016, Calendar.FEBRUARY, 29).getTime(),
                createLocalDate(2016, Calendar.MARCH, 7).getTime()
        ), r6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomeWeeksNullMonth() {
        latestSomeDays(null, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomWeeksNegativeMonths() {
        final Date d1 = createLocalDate(2018, Calendar.MAY, 31, 10, 10, 10).getTime();
        latestSomeDays(d1, -10);
    }


    @Test
    public void testLatestSomeMonths() {
        final Date d1 = createLocalDate(2018, Calendar.MAY, 31, 10, 10, 10).getTime();
        final DateRange r1 = latestSomeMonths(d1, 2);
        final DateRange r2 = latestSomeMonths(d1, 10);
        final DateRange r3 = latestSomeMonths(d1, 0);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.MARCH, 31).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r1);
        assertEquals(new DateRange(
                createLocalDate(2017, Calendar.JULY, 31).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r2);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.MAY, 31).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomeMonthsNullDay() {
        latestSomeMonths(null, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomMonthsNegativeDays() {
        final Date d1 = createLocalDate(2018, Calendar.MAY, 31, 10, 10, 10).getTime();
        latestSomeMonths(d1, -2);
    }

    @Test
    public void testLatestSomeYears() {
        final Date d1 = createLocalDate(2018, Calendar.MAY, 31, 10, 10, 10).getTime();
        final DateRange r1 = latestSomeYears(d1, 2);
        final DateRange r2 = latestSomeYears(d1, 0);
        assertEquals(new DateRange(
                createLocalDate(2016, Calendar.MAY, 31).getTime(),
                createLocalDate(2018, Calendar.MAY, 31).getTime()
        ), r1);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.JANUARY, 1).getTime(),
                createLocalDate(2018, Calendar.DECEMBER, 31).getTime()
        ), r2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomeYearsNullYear() {
        latestSomeYears(null, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatestSomYearsNegativeYears() {
        final Date d1 = createLocalDate(2018, Calendar.MAY, 31, 10, 10, 10).getTime();
        latestSomeYears(d1, -2);
    }

    @Test
    public void testThisYear() {
        final Date d1 = createLocalDate(2000, Calendar.MAY, 1, 10, 10, 10).getTime();
        final DateRange r1 = thisYear(d1);
        assertEquals(new DateRange(
                createLocalDate(2000, Calendar.JANUARY, 1).getTime(),
                createLocalDate(2000, Calendar.DECEMBER, 31).getTime()
        ), r1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThisYearNullYear() {
        thisYear(null);
    }

    @Test
    public void testThisWeak() {
        final Date d1 = createLocalDate(2018, Calendar.JUNE, 10, 10, 10, 10).getTime();
        final DateRange r1 = thisWeek(d1);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.JUNE, 4).getTime(),
                createLocalDate(2018, Calendar.JUNE, 10).getTime()
        ), r1);

        final Date d2 = createLocalDate(2018, Calendar.JUNE, 1, 10, 10, 10).getTime();
        final DateRange r2 = thisWeek(d2);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.MAY, 28).getTime(),
                createLocalDate(2018, Calendar.JUNE, 3).getTime()
        ), r2);

        final Date d3 = createLocalDate(2017, Calendar.JANUARY, 1, 10, 10, 10).getTime();
        final DateRange r3 = thisWeek(d3);
        assertEquals(new DateRange(
                createLocalDate(2016, Calendar.DECEMBER, 26).getTime(),
                createLocalDate(2017, Calendar.JANUARY, 1).getTime()
        ), r3);

        final Date d4 = createLocalDate(2018, Calendar.JUNE, 4, 10, 10, 10).getTime();
        final DateRange r4 = thisWeek(d4);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.JUNE, 4).getTime(),
                createLocalDate(2018, Calendar.JUNE, 10).getTime()
        ), r4);

        final Date d5 = createLocalDate(2018, Calendar.JUNE, 10, 10, 10, 10).getTime();
        final DateRange r5 = thisWeek(d5);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.JUNE, 4).getTime(),
                createLocalDate(2018, Calendar.JUNE, 10).getTime()
        ), r5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThisWeakNullWeak() {
        thisWeek(null);
    }

    @Test
    public void testThisMonth() {
        final Date d1 = createLocalDate(2018, Calendar.JUNE, 8, 10, 10, 10).getTime();
        final DateRange r1 = thisMonth(d1);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.JUNE, 1).getTime(),
                createLocalDate(2018, Calendar.JUNE, 30).getTime()
        ), r1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThisMonthNullMonth() {
        thisMonth(null);
    }


    @Test
    public void testToday() {
        final Date d1 = createLocalDate(2000, Calendar.MAY, 1, 10, 10, 10).getTime();
        final DateRange r1 = today(d1);
        assertEquals(new DateRange(
                createLocalDate(2000, Calendar.MAY, 1).getTime(),
                createLocalDate(2000, Calendar.MAY, 1).getTime()
        ), r1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTodayNullDay() {
        today(null);
    }


    @Test
    public void testTomorrow() {
        final Date d1 = createLocalDate(2000, Calendar.MAY, 1, 10, 10, 10).getTime();
        final DateRange r1 = tomorrow(d1);
        assertEquals(new DateRange(
                createLocalDate(2000, Calendar.MAY, 2, 10, 10, 10).getTime(),
                createLocalDate(2000, Calendar.MAY, 2, 10, 10, 10).getTime()
        ), r1);

        final Date d2 = createLocalDate(2016, Calendar.FEBRUARY, 29, 10, 10, 10).getTime();
        final DateRange r2 = tomorrow(d2);
        assertEquals(new DateRange(
                createLocalDate(2016, Calendar.MARCH, 1, 10, 10, 10).getTime(),
                createLocalDate(2016, Calendar.MARCH, 1, 10, 10, 10).getTime()
        ), r2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTomorrowNullDay() {
        tomorrow(null);
    }


    @Test
    public void testYesterday() {
        final Date d1 = createLocalDate(2000, Calendar.MAY, 2, 10, 10, 10).getTime();
        final DateRange r1 = yesterday(d1);
        assertEquals(new DateRange(
                createLocalDate(2000, Calendar.MAY, 1, 10, 10, 10).getTime(),
                createLocalDate(2000, Calendar.MAY, 1, 10, 10, 10).getTime()
        ), r1);

        final Date d2 = createLocalDate(2016, Calendar.MARCH, 1, 10, 10, 10).getTime();
        final DateRange r2 = yesterday(d2);
        assertEquals(new DateRange(
                createLocalDate(2016, Calendar.FEBRUARY, 29, 10, 10, 10).getTime(),
                createLocalDate(2016, Calendar.FEBRUARY, 29, 10, 10, 10).getTime()
        ), r2);

        final Date d3 = createLocalDate(2018, Calendar.MARCH, 1, 10, 10, 10).getTime();
        final DateRange r3 = yesterday(d3);
        assertEquals(new DateRange(
                createLocalDate(2018, Calendar.FEBRUARY, 28, 10, 10, 10).getTime(),
                createLocalDate(2018, Calendar.FEBRUARY, 28, 10, 10, 10).getTime()
        ), r3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testYesterdayNullDay() {
        yesterday(null);
    }
}
