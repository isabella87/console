package com.banhui.console.ui;

import com.banhui.console.rpc.FileRef;
import com.banhui.console.rpc.OfsClient;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.prompt;

public class ChooseProtocolDlg
        extends DialogPane {
    private volatile long id;
    private volatile int fileType;
    private volatile int chooseType;
    private volatile String filePath;

    public ChooseProtocolDlg(
            long id,
            int fileType,
            int chooseType
    ) {
        this.id = id;
        this.fileType = fileType;
        this.chooseType = chooseType;
        if (this.id != 0) {
            setTitle(getTitle() + id + "/" + fileType);
        }
        controller().disable("delete");
        controller().disable("download");
        controller().connect("list", "change", this::listChanged);
        controller().connect("upload", this::upload);
        controller().connect("refresh", this::refresh);
        controller().connect("delete", this::delete);
        controller().connect("download", this::download);
        controller().call("refresh");

        TableColumnModel tcm = controller().get(JTable.class, "list").getColumnModel();
        TableColumn hashColumn = tcm.getColumn(3);
        TableColumn idColumn = tcm.getColumn(4);
        tcm.removeColumn(hashColumn);
        tcm.removeColumn(idColumn);
    }

    private void download(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long fileId = tableModel.getNumberByName(table.getSelectedRow(), "id");
        final String fileHash = tableModel.getStringByName(table.getSelectedRow(), "hash");
        final String fileName = tableModel.getStringByName(table.getSelectedRow(), "name");

        OfsClient ofsClient = new OfsClient();
        String path = new FileUtil(null).choiceDirToSave(fileName);
        this.filePath = path;
        if (path != null) {
            try {
                ofsClient.download(fileId, fileHash, path);
                prompt(null, controller().getMessage("download-success"));
            } catch (IOException e) {
                prompt(null, controller().getMessage("download-failed"));
                e.printStackTrace();
            }
        }
    }

    private void delete(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        int[] selectedRows = table.getSelectedRows();
        StringBuilder fileName = new StringBuilder();
        for (int selectedRow : selectedRows) {
            final String name = tableModel.getStringByName(selectedRow, "name");
            fileName.append("、").append(name);
        }
        String confirmDelete = controller().formatMessage("confirm-delete", fileName.toString());
        int a = 0;
        if (confirm(null, confirmDelete)) {
            boolean flag;
            for (int selectedRow : selectedRows) {
                final long fileId = tableModel.getNumberByName(selectedRow, "id");
                final String fileHash = tableModel.getStringByName(selectedRow, "hash");
                OfsClient ofsClient = new OfsClient();
                flag = ofsClient.unlink(fileId, fileHash);
                if (!flag) {
                    a++;
                }
            }
            if (a == 0) {
                controller().call("refresh");
                prompt(null, controller().getMessage("delete-success"));
            } else {
                prompt(null, controller().getMessage("delete-fail"));
            }
            controller().disable("delete");
        }
    }


    private void refresh(
            ActionEvent actionEvent
    ) {
        OfsClient ofsClient = new OfsClient();
        try {
            List<FileRef> frList = ofsClient.list(id, (long) fileType);
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            List<Map<String, Object>> list = new ArrayList<>();
            for (FileRef fr : frList) {
                Map<String, Object> param = new HashMap<>();
                param.put("name", fr.getName());
                long size = fr.getSize();
                DecimalFormat df = new DecimalFormat("#.0");
                String sizeByte;
                if (size > 1024) {
                    sizeByte = df.format((double) size / 1024) + " KB";
                } else {
                    sizeByte = df.format((double) size) + " B";
                }
                param.put("size", sizeByte);
                param.put("lastModifiedTime", fr.getLastModifiedTime());
                param.put("hash", fr.getHash());
                param.put("id", fr.getId());
                list.add(param);
            }
            tableModel.setAllRows(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upload(
            ActionEvent actionEvent
    ) {
        FileUtil fileUploadUtil = new FileUtil(chooseType);
        InputStream uploadFileContent = fileUploadUtil.getUploadFileInputString();
        String uploadFileName = fileUploadUtil.getUploadFileName();
        if (uploadFileName != null && !uploadFileName.isEmpty() && uploadFileContent != null) {
            OfsClient ofsClient = new OfsClient();
            try {
                ofsClient.upload(id, fileType, uploadFileName, null, fileUploadUtil.getFileSize(), uploadFileContent);
                controller().call("refresh");
                prompt(null, controller().getMessage("upload-success"));
            } catch (IOException e) {
                ErrorHandler.handle(e);
            }
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "list").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("delete");
            controller().enable("download");
        } else if (selectedRows.length > 1) {
            controller().disable("download");
        } else {
            controller().disable("delete");
            controller().disable("download");
        }
    }
}
