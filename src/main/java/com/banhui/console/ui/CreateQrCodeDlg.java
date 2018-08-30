package com.banhui.console.ui;


import com.google.zxing.WriterException;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ImageBox;

import static org.xx.armory.swing.DialogUtils.prompt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class CreateQrCodeDlg
        extends DialogPane {

    public CreateQrCodeDlg() {
        controller().setText("register-address", controller().getMessage("default-message"));
        controller().setText("code", controller().getMessage("default-code"));
        controller().connect("create", this::Create);
        controller().connect("add-logo", this::AddLogo);
        controller().connect("save", this::SaveImage);

        controller().readOnly("recommend-url", true);
    }

    private void SaveImage(
            ActionEvent actionEvent
    ) {
        Image image = controller().get(ImageBox.class, "Qr-code").getData();
        if (image == null) {
            prompt(null, controller().getMessage("none-image"));
        } else {
            String path = new QrCodeUtil().choiceDirToSave();
            new QrCodeUtil().outputImage(path, (BufferedImage) image);
        }
    }

    private void AddLogo(
            ActionEvent actionEvent
    ) {
        String path = new FileUtil(1).chooseFile();
        controller().setText("logo", path);
    }

    private void Create(
            ActionEvent actionEvent
    ) {
        String address = controller().getText("register-address");
        String code = controller().getText("code");
        if (code.isEmpty()) {
            prompt(null, controller().getMessage("none-code"));
            return;
        }
        try {
            code = URLEncoder.encode(code, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String recommendUrl = address + code;
        controller().setText("recommend-url", recommendUrl);

        Image image = null;
        String logo = controller().getText("logo");
        try {
            image = new QrCodeUtil().getQrCode(recommendUrl, logo);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        controller().get(ImageBox.class, "Qr-code").setData(image);
    }
}
