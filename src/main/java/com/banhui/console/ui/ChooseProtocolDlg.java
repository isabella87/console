package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.DialogUtils;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
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

        String path = new FileUtil(null).choiceDirToSave(fileName);
        this.filePath = path;
        if (path != null) {
            final Map<String, Object> params = new HashMap<>();
            params.put("id", fileId);
            params.put("hash", fileHash);
            new ProjectProxy().downloadProtocol(params)
                              .thenApplyAsync(Result::stringValue)
                              .whenCompleteAsync(this::downloadCallBack, UPDATE_UI);
        }
    }

    private void downloadCallBack(
            String fileEncodeContent,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else if (fileEncodeContent != null) {
            if (new FileUtil(null).saveDownloadFile(fileEncodeContent, filePath)) {
                prompt(null, controller().getMessage("download-success"));
            } else {
                prompt(null, controller().getMessage("download-failed"));
            }
        }
    }


    private void delete(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final String fileName = tableModel.getStringByName(table.getSelectedRow(), "name");
        final long fileId = tableModel.getNumberByName(table.getSelectedRow(), "id");
        final String fileHash = tableModel.getStringByName(table.getSelectedRow(), "hash");

        String confirmDelete = controller().formatMessage("confirm-delete", fileName);
        if (confirm(null, confirmDelete)) {
            controller().disable("delete");
            final Map<String, Object> params = new HashMap<>();
            params.put("object-id", id);
            params.put("file-type", fileType);
            params.put("id", fileId);
            params.put("hash", fileHash);
            new ProjectProxy().delProtocol(params)
                              .thenApplyAsync(Result::map)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            Map<String, Object> deletedRow,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().call("refresh");
            prompt(null, controller().getMessage("delete-success"));
        }
        controller().disable("delete");
    }

    private void refresh(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("object-id", id);
        params.put("file-type", fileType);
        new ProjectProxy().protocolList(params)
                          .thenApplyAsync(Result::list)
                          .thenAcceptAsync(this::searchCallback, UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        for (Map<String, Object> map : c) {
            if (!map.isEmpty() && map.get("size") != null) {
                int size = (int) map.get("size");
                String sizeByte;
                if (size > 1024) {
                    sizeByte = size / 1024 + " KB";
                } else {
                    sizeByte = size + " B";
                }
                map.put("size", sizeByte);
            }
        }
        tableModel.setAllRows(c);
    }

    private void upload(
            ActionEvent actionEvent
    ) {
        FileUtil fileUploadUtil = new FileUtil(chooseType);
        String uploadFileContent = fileUploadUtil.getUploadFileContent();
        String uploadFileName = fileUploadUtil.getUploadFileName();
        if (uploadFileName != null && !uploadFileName.isEmpty() && uploadFileContent != null && !uploadFileContent.isEmpty()) {
            final Map<String, Object> params = new HashMap<>();
            params.put("object-id", id);
            params.put("file-type", fileType);
            params.put("file-name", uploadFileName);
            params.put("file-code", uploadFileContent);
            new ProjectProxy().uploadBill(params)
                              .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        }
//        else {
//            DialogUtils.confirm(null, controller().getMessage("upload-file-confirm"));
//        }
    }

    private void saveCallback(
            Result result,
            Throwable t
    ) {

        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().call("refresh");
            prompt(null, controller().getMessage("upload-success"));
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
            controller().disable("delete");
            controller().disable("download");
        } else {
            controller().disable("delete");
            controller().disable("download");
        }
    }
}
