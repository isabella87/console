package com.banhui.console.ui;


import com.banhui.console.rpc.AuthenticationProxy;
import com.banhui.console.rpc.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ImageBox;

import javax.swing.*;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.focusInWindow;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

/**
 * 登录对话框。
 */
public class SignInDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(SignInDlg.class);

    public SignInDlg() {
        super();

        controller().connect("captcha-image", "click", this::captchaImageClick);

        updateCaptchaImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            final String loginName = controller().getText("login-name").trim();
            final String password = controller().getText("password").trim();
            final String captchaCode = controller().getText("captcha-code").trim();

            if (loginName.isEmpty() || password.isEmpty() || captchaCode.isEmpty()) {
                focusInWindow(controller().get(JComponent.class, "login-name"));
                return;
            }

            final Map<String, Object> params = new HashMap<>();
            params.put("user-name", loginName);
            params.put("password", password);
            params.put("captcha-code", captchaCode);

            controller().disable("ok");

            new AuthenticationProxy().signIn(params)
                                     .thenApplyAsync(r -> r.booleanValue().orElse(false))
                                     //.exceptionally(UIUtils::defaultDumpThrowable)
                                     .whenCompleteAsync(this::signInCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void captchaImageClick(
            Object event
    ) {
        updateCaptchaImage();
    }

    private void updateCaptchaImage() {
        new AuthenticationProxy().captchaImage()
                                 .thenApplyAsync(ResultUtils::readImage)
                                 .thenAcceptAsync(this::updateCaptchaImageCallback, UPDATE_UI)
                                 .exceptionally(ErrorHandler::handle);
    }

    private void updateCaptchaImageCallback(
            Image image
    ) {
        controller().get(ImageBox.class, "captcha-image").setData(image);
        getOwner().pack();
    }

    private void signInCallback(
            Boolean result,
            Throwable throwable
    ) {
        if (throwable != null) {
            ErrorHandler.handle(throwable);
        }

        if (result) {
            super.done(OK);
        } else {
            fail(controller().getMessage("fail"));

            controller().setText("password", null);
            controller().setText("captcha-code", null);
            focusInWindow(controller().get(JComponent.class, "password"));
        }

        controller().enable("ok");
    }
}
