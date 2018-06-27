package com.banhui.console.ui;


import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.DialogPane;

/**
 * 选项设置对话框。
 */
public class SettingsDlg
        extends DialogPane {
    public SettingsDlg() {
        controller().setText("rpc-base-uri", Application.settings().getProperty("rpc-base-uri"));

        controller().connect("", "verify-error", MsgUtils::verifyError);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            Application.settings().setProperty("rpc-base-uri", controller().getText("rpc-base-uri"));
        }

        super.done(result);
    }
}
