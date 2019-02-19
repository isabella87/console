package com.banhui.console.ui;

import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BaseFramePane extends InternalFramePane {
    public String getExport_title() {
        return export_title;
    }

    public TypedTableModel getTypedTableModel() {
        return typedTableModel;
    }

    private String export_title;
    private TypedTableModel typedTableModel;

    public void setTableTitleAndTableModelForExport(
            String thisExportTableTile,
            TypedTableModel thisExportTableModel
    ) {
        this.export_title = thisExportTableTile;
        this.typedTableModel = thisExportTableModel;
    }

    public void setExport_title(String export_title) {
        this.export_title = export_title;
    }

    public void setTypedTableModel(TypedTableModel typedTableModel) {
        this.typedTableModel = typedTableModel;
    }

    public void export() {
        new ExcelExportUtil(export_title, typedTableModel).choiceDirToSave(export_title);
    }

    /**
     * 只有双击首列才有构造“双击选中整行的效果”,
     * 首次双击选中全列后，再单击其他列可实现选中任意多列。
     *
     * @param jTable
     */
    public void doubleClickEvent(JTable jTable) {
        jTable.addMouseListener(new MouseAdapter() {


            @Override
            public void mouseClicked(MouseEvent e) {
                int[] rows = jTable.getSelectedRows();
                if (rows.length > 0 && e.getClickCount() == 2) {
                    for (int row : rows) {
                        if (jTable.isCellSelected(row, 0)) {
                            for (int j = jTable.getColumnCount() - 1; j >= 0; j--) {
                                jTable.changeSelection(row, j, true, true);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 搜索框获得焦点时，显示提示LABEL
     */
    public void showTipLabel() {
        JTextField textField = controller().get(JTextField.class, "search-key");
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                controller().show("tip");
            }

            @Override
            public void focusLost(FocusEvent e) {
                controller().hide("tip");
            }
        });
    }
}
