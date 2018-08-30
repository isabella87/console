package com.banhui.console.ui;


import org.xx.armory.swing.Application;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class FileUtil {
    private Integer chooseType;

    private String uploadFileName;

    private String uploadFilePath;

    public FileUtil(
            Integer chooseType
    ) {
        this.chooseType = chooseType;
    }

    public String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        FileFilter imageFilter = new FileNameExtensionFilter("图片文件 files(*.jpg;*.png;*.jpeg;*.gif)", "jpg", "png", "jpeg", "gif");
        FileFilter pdfFilter = new FileNameExtensionFilter("PDF文档 file(*.pdf)", "pdf");

        switch (chooseType) {
            case 1:
                fileChooser.addChoosableFileFilter(imageFilter);
                fileChooser.setFileFilter(imageFilter);
                break;
            case 2:
                fileChooser.addChoosableFileFilter(pdfFilter);
                fileChooser.setFileFilter(pdfFilter);
                break;
        }
        if (fileChooser.showDialog(Application.mainFrame(), "") == JFileChooser.APPROVE_OPTION) {
            uploadFileName = fileChooser.getSelectedFile().getName();
            uploadFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public String getUploadFileContent() {

        chooseFile();

        String encodeContent = "";
        if (uploadFilePath != null) {
            File file = new File(uploadFilePath);
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
            encodeContent = Base64.getEncoder().encodeToString(buffer);
        }
        return encodeContent;
    }


    public String choiceDirToSave(String fileName) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("另存为");
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File(fileName));

        if (fileChooser.showSaveDialog(Application.mainFrame()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public boolean saveDownloadFile(
            String fileEncodeContent,
            String filePath
    ) {
        boolean flag = false;

        byte[] buffer = Base64.getDecoder().decode(fileEncodeContent);
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
            flag = true;

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
        return flag;
    }
}
