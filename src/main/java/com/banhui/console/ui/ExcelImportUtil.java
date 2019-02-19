package com.banhui.console.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.TypedTableColumn;
import org.xx.armory.swing.components.TypedTableColumnType;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import static org.xx.armory.swing.DialogUtils.prompt;

/**
 * @author Isabella
 */
public class ExcelImportUtil {
    private POIFSFileSystem fs;
    private Sheet sheet;
    private Row row;
    private Workbook wb;
    List<String> firstRowCelLValues = new ArrayList<>();
    Map<String, String> columnTitleAndName = new HashMap<>();
    Map<String, TypedTableColumnType> columnTitleAndType = new HashMap<>();

    public List<Map<String, Object>> getDatas() {
        return datas;
    }

    List<Map<String, Object>> datas;

    public ExcelImportUtil(TypedTableModel tableModel) {
        initColumnNameAndTitleByTableModule(tableModel);

    }

    public List<Map<String, Object>> readExcel() {
        String path = choiceFile();
        if (path == null) {
            return new ArrayList<>();
        }
        try (InputStream is = new FileInputStream(path)) {
            wb = WorkbookFactory.create(is);
            if (path.endsWith("xls")) {
                fs = new POIFSFileSystem(is);
                wb = new HSSFWorkbook(fs);
            } else if (path.endsWith("xlsx")) {
                wb = new XSSFWorkbook(is);
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        if (wb != null) {
            sheet = wb.getSheetAt(0);
        }
        readExcelTitle();
        return readExcelContent();
    }

    /**
     * 读取Excel表格表头的内容
     *
     * @return String 表头内容的数组
     */
    public void readExcelTitle() {
        row = sheet.getRow(0);
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            firstRowCelLValues.add((row.getCell((short) i)).getStringCellValue());
        }
    }

    /**
     * 读取Excel数据内容
     *
     * @return Map 包含单元格数据内容的Map对象
     */
    public List<Map<String, Object>> readExcelContent() {

        datas = new ArrayList<>();
        //HSSFCellStyle cellStyle = ExcelColumnStyle.titleStyle(wb, null);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            for (int j = 0; j < firstRowCelLValues.size(); j++) {
                Cell cell = row.getCell((short) j);
                if (cell == null) {
                    continue;
                }
                Object content = getStringCellValue(cell, j);
//                Object content = getCellValue(cell);  输出跟在输入在类型上有变动，不好用
                map.put(columnTitleAndName.get(firstRowCelLValues.get(j).split("-")[0]), content);
            }
            datas.add(map);
        }
        return datas;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell
     *         Excel单元格
     * @return String 单元格数据内容
     */
    private Object getStringCellValue(
            Cell cell,
            int column
    ) {
        Object strCell = null;
        Object obj;
        String cellStr = cell.toString();
        if (cellStr != null && !cellStr.isEmpty()) {
            String cellTitle = firstRowCelLValues.get(column).trim();
            TypedTableColumnType columnType = columnTitleAndType.get(cellTitle.split("-")[0]);
            if (columnType == null) {
                prompt(null, "文件格式错误，列名未对应！！！");
            }
            //String a = cell.getCellTypeEnum().toString();
            assert columnType != null;
            switch (columnType) {
                case TEXT:
                    if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        DecimalFormat df = new DecimalFormat("0");//使用DecimalFormat类对科学计数法格式的数字进行格式化
                        strCell = df.format(cell.getNumericCellValue());
                    } else {
                        strCell = cell.getStringCellValue();
                    }
                    break;
                case NUMBER:
                    obj = getCellValue(cell);
                    strCell = Integer.valueOf(obj.toString());
                    break;
                case DATE_TIME:
                    strCell = cell.getDateCellValue();
                    break;
                case DATE:
                    strCell = cell.getDateCellValue();
                    break;
                case CURRENCY:
                case FLOAT:
                    obj = getCellValue(cell);
                    strCell = Float.valueOf(obj.toString());
                    break;
                case YES_OR_NO:
                    obj = getCellValue(cell);
                    strCell = Boolean.valueOf(obj.toString());
                    break;
                default:
                    strCell = "";
                    break;
            }
        }
        return strCell;
    }

    private Object getCellValue(Cell cell) {
        Object obj;
        CellType cellType = cell.getCellTypeEnum();
        if (cellType == CellType.NUMERIC) {
            obj = cell.getNumericCellValue();
        } else if (cellType == CellType.STRING) {
            obj = cell.getStringCellValue();
        } else if (cellType == CellType.BOOLEAN) {
            obj = cell.getBooleanCellValue();
        } else {
            obj = cell.getDateCellValue();
        }
        return obj;
    }

    public String choiceFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:/Users/Administrator/Desktop/"));
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

        FileFilter xlsFilter = new FileNameExtensionFilter("xls file(*.xls)", "xls");
        FileFilter xlsxFilter = new FileNameExtensionFilter("xlsx file(*.xlsx)", "xlsx");
        fileChooser.addChoosableFileFilter(xlsFilter);
        fileChooser.addChoosableFileFilter(xlsxFilter);
        fileChooser.setFileFilter(xlsFilter);

        if (fileChooser.showDialog(Application.mainFrame(), "") == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public void initColumnNameAndTitleByTableModule(TypedTableModel tableModel) {
        List<TypedTableColumn> columns = tableModel.getAllColumns();
        for (TypedTableColumn column : columns) {
            String columnTitle = column.getTitle().trim();
            if (columnTitle.isEmpty()) {
                continue;
            }
            columnTitleAndName.put(column.getTitle(), column.getName());
            columnTitleAndType.put(column.getTitle(), column.getType());
        }
    }
}
