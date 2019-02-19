package com.banhui.console.ui;


import com.banhui.console.rpc.AuthenticationProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.DialogPane;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

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
            String oldRpcBaseUrl = Application.settings().getProperty("rpc-base-uri");
            String newRpcBaseUrl = controller().getText("rpc-base-uri");
            Application.settings().setProperty("rpc-base-uri", newRpcBaseUrl);
            if(!oldRpcBaseUrl.equals(newRpcBaseUrl)){
               //清除远程服务器端token，并弹出登陆框。
                super.done(result);
                new AuthenticationProxy().signOut().whenCompleteAsync(this::signOutCallback,UPDATE_UI);
            }
        }

        super.done(result);
    }

    private void signOutCallback(
            Result result,
            Throwable t
    ) {
        new AuthenticationProxy().resetHttpBaseUrl();
        showModel(null, new SignInDlg());
    }
}
