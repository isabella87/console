package com.banhui.console.ui;


import org.xx.armory.swing.components.DialogPane;

import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class CreateQrCodeDlg
        extends DialogPane {

    public CreateQrCodeDlg() {
        controller().setText("register-address", controller().getMessage("default-message"));
        controller().setText("code", controller().getMessage("default-code"));
        controller().connect("create", this::Create);
        controller().connect("add-logo", this::AddLogo);

        controller().readOnly("recommend-url", true);
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
        try {
            code = URLEncoder.encode(code, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        controller().setText("recommend-url", address + code);
    }
}
