package com.banhui.console.ui;


import com.banhui.console.rpc.AuthenticationProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.commons.SysUtils.stringEquals;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

/**
 * 修改密码对话框。
 */
public class ChangePasswordDlg
        extends DialogPane {
    public ChangePasswordDlg() {
        setFixedSize(true);

        controller().disable("ok");

        new AuthenticationProxy().current()
                                 .thenApply(Result::map)
                                 .whenCompleteAsync(this::update, UPDATE_UI);
    }

    private void update(
            Map<String, Object> user,
            Throwable throwable
    ) {
        if (throwable != null) {
            ErrorHandler.handle(throwable);
            close();
        }

        controller().enable("ok");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final String oldPassword = controller().getText("old-password").trim();
            final String newPassword = controller().getText("new-password").trim();
            final String newPassword2 = controller().getText("new-password2").trim();

            if (!stringEquals(newPassword, newPassword2)) {
                warn(this.getOwner(), controller().getMessage("password-not-equal"));
                controller().get(Component.class, "new-password").requestFocusInWindow();
                controller().enable("ok");
                return;
            }

            final Map<String, Object> params = new HashMap<>();
            params.put("old-password", oldPassword);
            params.put("new-password", newPassword);

            new AuthenticationProxy().changePassword(params)
                                     .thenApply(Result::booleanValue)
                                     .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Boolean result,
            Throwable throwable
    ) {
        if (throwable != null) {
            ErrorHandler.handle(throwable);
        } else {
            if (result) {
                prompt(this.getOwner(), controller().getMessage("success"));
            } else {
                fail(this.getOwner(), controller().getMessage("fail"));
            }
        }

        controller().enable("ok");
    }

}
