package com.banhui.console.ui;

import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PermissionBatchInDlg
        extends DialogPane {
    private volatile long id;

    public PermissionBatchInDlg(
            long id
    ) {
        this.id = id;
        controller().connect("load", this::load);
//        controller().connect("delete",this::delete);
//        controller().connect("begin",this::begin);
//        controller().connect("stop",this::stop);
    }

    private void load(
            ActionEvent actionEvent
    ) {
        controller().disable("load");
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        controller().enable("load");
    }
}
