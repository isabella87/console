package com.banhui.console.ui;

import org.xx.armory.swing.components.DialogPane;

import javax.swing.*;

public class ChooseBillFileDlg
        extends DialogPane {
    private volatile long id;

    public ChooseBillFileDlg(
            long id
    ) {
        this.id = id;
        if (this.id != 0) {
            setTitle(getTitle() + id);
        }
        controller().disable("delete");
        controller().disable("download");
        controller().connect("list", "change", this::listChanged);
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("delete");
            controller().enable("download");
        } else if (selectedRows.length > 1) {
            controller().disable("delete");
            controller().disable("download");
        } else {
            controller().disable("delete");
            controller().disable("download");
        }
    }
}
