package com.banhui.console.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.TypedTableColumn;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * EXCEL报表工具类.
 */
public class ExcelExportUtil {

    private HSSFWorkbook wb;
    private HSSFSheet sheet;

    private TypedTableModel tableModel;
    private final String TITLE = "_title";
    private final String HEAD = "_head";
    private String headTitle;
    private final int HEAD_ROW = 0;
    private final int TITLE_ROW = 1;

    private Map<String, HSSFCellStyle> cellStyleMap = new HashMap<>();

    HSSFCellStyle textCellStyle;
    HSSFCellStyle dateTimeCellStyle;
    HSSFCellStyle dateCellStyle;
    HSSFCellStyle numberCellStyle;
    HSSFCellStyle floatCellStyle;

    /**
     * @param sheetName
     * @param tableModel
     */
    public ExcelExportUtil(
            String sheetName,
            TypedTableModel tableModel
    ) {
        this.tableModel = tableModel;

        this.wb = new HSSFWorkbook();
        this.sheet = wb.createSheet(sheetName);
        this.headTitle = sheetName;

        init();
    }

    private void init() {

        cellStyleMap.put(TITLE, ExcelColumnStyle.titleStyle(wb, null));
        cellStyleMap.put(HEAD, ExcelColumnStyle.headStyle(wb, null));

        createHeadTitleRow();

        createTitleRow();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            createDataRow(i + 1);
        }
    }

    private void createHeadTitleRow(
    ) {
        boolean flag = false;
        List<TypedTableColumn> cloumns = tableModel.getAllColumns();
        for (TypedTableColumn column : cloumns) {
            String columnName = column.getName();
            if (columnName == null || columnName.isEmpty() || columnName.equals("_row")) {
                flag = true;
                break;
            }
        }
        HSSFRow row = sheet.createRow(HEAD_ROW);
        row.setHeight((short) 400);
        HSSFCell cell = row.createCell(flag ? 1 : 0);
        cell.setCellValue(new HSSFRichTextString(headTitle));
        cell.setCellStyle(cellStyleMap.get(HEAD));

        sheet.addMergedRegion(new CellRangeAddress(0, 0, flag ? 1 : 0, tableModel.getColumnCount() - 1));

    }

    public void createTitleRow() {
        // 定义第一行
        HSSFRow row = sheet.createRow(TITLE_ROW);

        final List<TypedTableColumn> columns = tableModel.getAllColumns();
        //冻结首行
        sheet.createFreezePane(columns.size(), TITLE_ROW + 1);

        for (int i = 0; i < columns.size(); ++i) {
            final TypedTableColumn column = columns.get(i);

            String columnName = column.getName();
            if (columnName == null || columnName.isEmpty() || columnName.equals("_row")) {
                continue;
            }

            switch (column.getType()) {
                case DATE:
                    cellStyleMap.put(columnName, ExcelColumnStyle.dateStyle(wb, dateCellStyle));
                    break;
                case DATE_TIME:
                    dateTimeCellStyle = ExcelColumnStyle.dateTimeStyle(wb, dateTimeCellStyle);
                    cellStyleMap.put(columnName, dateTimeCellStyle);
                    break;
                case NUMBER:
                    numberCellStyle = ExcelColumnStyle.numberStyle(wb, numberCellStyle);
                    cellStyleMap.put(columnName, numberCellStyle);
                    break;
                case PERCENT:
                    /*cellStyleMap.put(columnName, ExcelColumnStyle.percentStyle(wb));
                    break;*/
                case CURRENCY:
                   /* cellStyleMap.put(columnName, ExcelColumnStyle.currencyStyle(wb));
                    break;*/
                case FLOAT:
                    floatCellStyle = ExcelColumnStyle.floatStyle(wb, floatCellStyle);
                    cellStyleMap.put(columnName, floatCellStyle);
                    break;
                case TEXT:
                   /* cellStyleMap.put(columnName, ExcelColumnStyle.textStyle(wb));
                    break;*/
                case CHECK_BOX:
                   /* cellStyleMap.put(columnName, ExcelColumnStyle.textStyle(wb));
                    break;*/
                case YES_OR_NO:
                   /* cellStyleMap.put(columnName, ExcelColumnStyle.textStyle(wb));
                    break;*/
                case IMAGE:
                    textCellStyle = ExcelColumnStyle.textStyle(wb, textCellStyle);
                    cellStyleMap.put(columnName, textCellStyle);
                    break;
            }

            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyleMap.get(TITLE));

            cell.getSheet().setColumnWidth(i, column.getSize() * 50);

            cell.setCellValue(column.getTitle());

        }
    }

    /**
     * 此处数据行从excel的第二行开始，rowNo值从1开始
     *
     * @param rowNo
     */
    public void createDataRow(
            int rowNo
    ) {
        HSSFRow row = sheet.createRow(TITLE_ROW + rowNo);

        final List<TypedTableColumn> columns = tableModel.getAllColumns();
        for (int i = 0; i < columns.size(); ++i) {
            final TypedTableColumn column = columns.get(i);

            String columnName = column.getName();
            if (columnName == null || columnName.isEmpty() || columnName.equals("_row")) {
                continue;
            }

            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyleMap.get(columnName));
            Object value = tableModel.getValueAt(rowNo - 1, i);
            if (value == null) {
                cell.setCellValue("");
            } else {
                switch (column.getType()) {
                    case DATE:
                    case DATE_TIME:
                        cell.setCellValue((Date) value);
                        break;
                    case NUMBER:
                    case FLOAT:
                    case PERCENT:
                    case CURRENCY:
                        cell.setCellValue(((Number) value).doubleValue());
                        break;
                    case TEXT:
                        cell.setCellValue(value.toString());
                        break;
                    case CHECK_BOX:
                        cell.setCellValue((Boolean) value);
                        break;
                    case YES_OR_NO:
                        cell.setCellValue((Boolean) value);
                        break;
                    case IMAGE:
                        cell.setCellValue(new HSSFRichTextString(""));
                        break;
                    /*default:
                        throw new Exception("can not find the column type!");*/
                }
            }
        }

    }

    /**
     * 输入EXCEL文件
     *
     * @param fileName
     *         文件名
     */
    public void outputExcel(String fileName) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(fileName));
            wb.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void choiceDirToSave() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("另存为");
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

        FileFilter xlsFilter = new FileNameExtensionFilter("xls file(*.xls)", "xls");
        fileChooser.addChoosableFileFilter(xlsFilter);
        fileChooser.setFileFilter(xlsFilter);

        if (fileChooser.showSaveDialog(Application.mainFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            outputExcel(file.getAbsolutePath() + ".xls");

        }
    }
}
