package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
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
    private volatile String filePath;

    public ChooseProtocolDlg(
            long id,
            int fileType
    ) {
        this.id = id;
        this.fileType = fileType;
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

        String path = new PictureImportUtil().choiceDirToSave(fileName);
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
            String fileCode,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else if (fileCode != null) {
            byte[] buffer = Base64.getDecoder().decode(fileCode);
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new ByteArrayInputStream(buffer);
                os = new FileOutputStream(filePath);
                byte[] by = new byte[1024];
                int rc;
                while ((rc = is.read(by, 0, by.length)) > 0) {
                    os.write(by, 0, rc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            prompt(null, controller().getMessage("download-success"));
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
            tableModel.setAllRows(c);
        }
    }

    private void upload(
            ActionEvent actionEvent
    ) {
        String filePath = new PictureImportUtil().chooseFile(fileType);
        if (filePath != null) {
            File file = new File(filePath);
            InputStream is;
            ByteArrayOutputStream os;
            byte[] buffer = null;
            try {
                is = new FileInputStream(file);
                os = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int rc;
                while ((rc = is.read(buff, 0, buff.length)) > 0) {
                    os.write(buff, 0, rc);
                }
                buffer = os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String encode = Base64.getEncoder().encodeToString(buffer);
            String fileName = file.getName();

            final Map<String, Object> params = new HashMap<>();
            params.put("object-id", id);
            params.put("file-type", fileType);
            params.put("file-name", fileName);
            params.put("file-code", encode);
            new ProjectProxy().uploadBill(params)
                              .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        }
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
