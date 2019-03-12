package com.banhui.console.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.intValue;

public class PrjUtils {

    /**
     * 设置提醒时间相对域值，以天为单位
     *
     * 截止还本到期天数
     */
    public static int[] REPAY_CAPITAL_DAYS = {0, 3};//3代表前3天，0代表当天

    /**
     * 截止最近一次还息天数
     */
    public static int[] REPAY_INTEREST_DAYS = {0, 5, 15, 30};

    /**
     * 截止票据到期天数
     */
    public static int[] BILL_DUE_DATE_DAYS = {5, 15};

    /**
     * 截止登记到期天数
     */
    public static int[] CHECKIN_DUE_DATE_DAYS = {5, 15};

    /**
     * 依次判断项目是否处于特殊时期的项目，
     * 如果是高亮还息项目，则返回红色；
     * 如果是高亮还本项目，则返回黄色；
     * 如果是高亮票据到期项目，则返回蓝色；
     * 如果是高亮登记到期项目，则返回粉红色；
     * 否则，返回黑色，（特殊设置为黑色时不改变原来颜色）。
     *
     * @return
     */
    public static Color getColorOfPrj(Collection<Map<String, Object>> repayBonus) {
        for (Map<String, Object> bonuMap : repayBonus) {
            Date dueDate = dateValue(bonuMap, "dueDate");
            int tranType = intValue(bonuMap, "tranType");
            if (dueDate.after(new Date())) {
                if (tranType == 1) {
                    return prjHighLightForRepayCapital(dueDate);
                } else {
                    return prjHightLightForRepayInterest(dueDate);
                }
            }
        }

        return Color.BLACK;
    }

    public static Color prjHighLightForRepayCapital(Date date) {

        for (int days : REPAY_CAPITAL_DAYS) {
            if (InputUtils.dateDomainValue(new Date(), date) == days) {
                return Color.YELLOW;
            }
        }
        return Color.BLACK;
    }

    public static Color prjHightLightForRepayInterest(Date date) {

        for (int days : REPAY_INTEREST_DAYS) {
            if (InputUtils.dateDomainValue(new Date(), date) == days) {
                return Color.RED;
            }
        }
        return Color.BLACK;
    }

    public static void setOneRowBackgroundColor(
            JTable table,
            int rowIndex,
            Color color
    ) {
        try {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

                public Component getTableCellRendererComponent(
                        JTable table,
                        Object value,
                        boolean isSelected,
                        boolean hasFocus,
                        int row,
                        int column
                ) {
                    Color bgColor = getBackground();
                    Color fgColor =getForeground();

                    if (row == rowIndex) {
                        setBackground(color);
                        setForeground(Color.WHITE);
                    } else {
                        if (row % 2 == 0) {
                            setBackground(Color.WHITE);
                            setForeground(Color.BLACK);
                        } else {
                            setBackground(Color.getHSBColor(0.5f, 0.1f, 1.0f));
                            setForeground(Color.BLACK);
                        }

                    }

                    return super.getTableCellRendererComponent(table, value,
                                                               isSelected, hasFocus, row, column);
                }
            };
            int columnCount = table.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        float[] ab = {0.0f, 0.0f, 0.0f};
        float[] a = Color.RGBtoHSB(216, 242, 242, ab);
        System.out.print(a[0] + ";" + a[1] + ";" + a[2]);
    }
}
