package com.banhui.console.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.TypedTableColumn;
import org.xx.armory.swing.components.TypedTableColumnType;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Isabella
 */
public class ExcelImportUtil {
    private POIFSFileSystem fs;
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private HSSFRow row;
    List<String> firstRowCelLValues = new ArrayList<>();
    Map<String, String> columnTitleAndName = new HashMap<>();
    Map<String, TypedTableColumnType> columnTitleAndType = new HashMap<String, org.xx.armory.swing.components.TypedTableColumnType>();

    public List<Map<String, Object>> getDatas() {
        return datas;
    }

    List<Map<String, Object>> datas;

    public ExcelImportUtil(TypedTableModel tableModel) {
        initColumnNameAndTitleByTableModule(tableModel);

    }

    public List<Map<String, Object>> readExcel() {
        String path = choiceFile();
        InputStream is;
        try {
            is = new FileInputStream(path);

            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = wb.getSheetAt(0);

        readExcelTitle();

        return readExcelContent();
    }

    /**
     * 读取Excel表格表头的内容
     *
     * @param
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
     * @param
     * @return Map 包含单元格数据内容的Map对象
     */
    public List<Map<String, Object>> readExcelContent() {

        datas = new ArrayList<>();

        HSSFCellStyle cellStyle = ExcelColumnStyle.titleStyle(wb, null);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();

        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();

            for (int j = 0; j < firstRowCelLValues.size(); j++) {
                HSSFCell cell = row.getCell((short) j);
                if (cell == null) {
                    continue;
                }

                Object content = getStringCellValue(cell, j);
//                Object content = getCellValue(cell);  输出跟在输入在类型上有变动，不好用

                map.put(columnTitleAndName.get( firstRowCelLValues.get(j).split("-")[0]), content);
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
            HSSFCell cell,
            int column
    ) {
        Object strCell = null;
        String cellStr = cell.toString();
        if (cellStr != null && !cellStr.isEmpty()) {
            String cellTitle = firstRowCelLValues.get(column);
            switch (columnTitleAndType.get(cellTitle.split("-")[0])) {
                case TEXT:
                    if(cellTitle.contains("-")){
                        strCell = (int) cell.getNumericCellValue();
                    }else{
                        strCell = cell.getStringCellValue();
                    }

                    break;
                case NUMBER:
                    strCell = (int) cell.getNumericCellValue();
                    break;
                case DATE_TIME:
                    strCell = cell.getDateCellValue();
                    break;
                case DATE:
                    strCell = cell.getDateCellValue();
                    break;
                case FLOAT:
                    strCell = cell.getNumericCellValue();
                    break;
                case YES_OR_NO:
                    strCell = cell.getBooleanCellValue();
                    break;
                default:
                    strCell = "";
                    break;
            }
        }
        return strCell;
    }

    private Object getCellValue(HSSFCell cell) {
        Object obj = null;
        int cellType = cell.getCellStyle().getDataFormat();
        if (cellType == 0) {
            obj = cell.getNumericCellValue();
        } else if (cellType == 1) {
            obj = cell.getStringCellValue();
        } else if (cellType == 2) {
            obj = cell.getStringCellValue();
        } else if (cellType == 4) {
            obj = cell.getBooleanCellValue();
        } else {
            obj = cell.getDateCellValue();
        }

        return obj;

    }

    public String choiceFile() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

        FileFilter xlsFilter = new FileNameExtensionFilter("xls file(*.xls)", "xls");
        FileFilter xlsxFilter = new FileNameExtensionFilter("xlsx file(*.xlsx)", "xlsx");
        fileChooser.addChoosableFileFilter(xlsFilter);
        fileChooser.addChoosableFileFilter(xlsxFilter);
        fileChooser.setFileFilter(xlsxFilter);
        fileChooser.setFileFilter(xlsFilter);


        if (fileChooser.showDialog(Application.mainFrame(), "") == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public void initColumnNameAndTitleByTableModule(TypedTableModel tableModel) {
        List<TypedTableColumn> columns = tableModel.getAllColumns();
        for (int i = 0; i < columns.size(); i++) {
            TypedTableColumn column = columns.get(i);
            columnTitleAndName.put(column.getTitle(), columns.get(i).getName());
            columnTitleAndType.put(column.getTitle(), columns.get(i).getType());
        }
    }
}
