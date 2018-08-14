package com.banhui.console.ui;


import org.xx.armory.swing.Application;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class PictureImportUtil {


    public String chooseFile(int fileType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        FileFilter jpgFilter = new FileNameExtensionFilter("jpg file(*.jpg)", "jpg");
        FileFilter pngFilter = new FileNameExtensionFilter("png file(*.png)", "png");
        FileFilter jpegFilter = new FileNameExtensionFilter("jpeg file(*.jpeg)", "jpeg");
        FileFilter gifFilter = new FileNameExtensionFilter("gif file(*.gif)", "gif");
        FileFilter pdfFilter = new FileNameExtensionFilter("pdf file(*.pdf)", "pdf");


        switch (fileType) {
            case 31:
                fileChooser.addChoosableFileFilter(jpgFilter);
                fileChooser.addChoosableFileFilter(pngFilter);
                fileChooser.addChoosableFileFilter(gifFilter);
                fileChooser.addChoosableFileFilter(jpegFilter);
                fileChooser.setFileFilter(jpegFilter);
                break;
            case 48:
                fileChooser.addChoosableFileFilter(pdfFilter);
                fileChooser.setFileFilter(pdfFilter);
                break;
            case 47:
                fileChooser.addChoosableFileFilter(pdfFilter);
                fileChooser.setFileFilter(pdfFilter);
                break;
        }
        if (fileChooser.showDialog(Application.mainFrame(), "") == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
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
}
