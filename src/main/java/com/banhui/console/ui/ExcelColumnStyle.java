package com.banhui.console.ui;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * EXCEL单元格样式类.
 */
public class ExcelColumnStyle {

    private static HSSFCellStyle baseStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {
        // 创建单元格样式
        if (cellStyle == null) {
            cellStyle = wb.createCellStyle();

            // 指定单元格居中对齐
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            // 指定单元格垂直居中对齐
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 指定当单元格内容显示不下时自动换行
            cellStyle.setWrapText(true);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THICK);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setShrinkToFit(true);

            // 设置单元格字体
            HSSFFont font = wb.createFont();
            font.setBold(true);
            font.setFontName("宋体");
            font.setFontHeight((short) 200);
            cellStyle.setFont(font);

            cellStyle.setShrinkToFit(true);
        }
        return cellStyle;
    }

    public static HSSFCellStyle titleStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {

        // 创建单元格样式
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setLocked(true);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
        cellStyle.setDataFormat((short) CellType.STRING.ordinal());

        return cellStyle;

    }

    public static HSSFCellStyle headStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {

        // 创建单元格样式
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setLocked(true);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.PINK.index);
        cellStyle.setDataFormat((short) CellType.STRING.ordinal());

        return cellStyle;

    }

    public static HSSFCellStyle textStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {

        // 创建单元格样式
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index);
        cellStyle.setDataFormat((short)CellType.STRING.ordinal());
        cellStyle.setUserStyleName("text-style");

        return cellStyle;
    }

    public static HSSFCellStyle numberStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {
        // 创建单元格样式
        cellStyle = baseStyle(wb, cellStyle);

        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.RED.index);
        cellStyle.setDataFormat((short) CellType.NUMERIC.ordinal());

        cellStyle.setUserStyleName("number-style");

        HSSFDataFormat df = wb.createDataFormat();

        cellStyle.setDataFormat(df.getFormat("#0"));
        return cellStyle;
    }

    public static HSSFCellStyle floatStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {

        // 创建单元格样式
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        cellStyle.setDataFormat((short) CellType.NUMERIC.ordinal());

        cellStyle.setUserStyleName("float-style");

        HSSFDataFormat df = wb.createDataFormat();

        cellStyle.setDataFormat(df.getFormat("#,##0.00"));

        return cellStyle;
    }

    public static HSSFCellStyle percentStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {
        // 创建单元格样式
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setUserStyleName("percent-style");

        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.BLUE.index);
        cellStyle.setDataFormat((short) CellType.NUMERIC.ordinal());
        HSSFDataFormat df = wb.createDataFormat();

        cellStyle.setDataFormat(df.getFormat("#,#0"));

        return cellStyle;
    }

    public static HSSFCellStyle currencyStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.index);
        cellStyle.setDataFormat((short) CellType.NUMERIC.ordinal());
        HSSFDataFormat df = wb.createDataFormat();
        cellStyle.setDataFormat(df.getFormat("#,##0.00"));
        cellStyle.setUserStyleName("currency-style");

        return cellStyle;
    }

    public static HSSFCellStyle dateStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setUserStyleName("date-style");
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.DARK_YELLOW.index);
        CreationHelper createHelper = wb.getCreationHelper();
        short dataFormat = createHelper.createDataFormat().getFormat("yyyy/mm/dd");
        cellStyle.setDataFormat(dataFormat);

        return cellStyle;
    }

    public static HSSFCellStyle dateTimeStyle(
            HSSFWorkbook wb,
            HSSFCellStyle cellStyle
    ) {
        cellStyle = baseStyle(wb, cellStyle);
        cellStyle.setUserStyleName("datetime-style");
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREEN.index);

        CreationHelper createHelper = wb.getCreationHelper();
        cellStyle.setDataFormat(
                createHelper.createDataFormat().getFormat("yyyy/mm/dd h:mm"));

        return cellStyle;
    }

}
