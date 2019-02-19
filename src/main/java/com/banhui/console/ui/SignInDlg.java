package com.banhui.console.ui;


import com.banhui.console.rpc.AuthenticationProxy;
import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ImageBox;

import javax.swing.*;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.xx.armory.swing.ComponentUtils.focusInWindow;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

/**
 * 登录对话框。
 */
public class SignInDlg
        extends DialogPane {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(SignInDlg.class);

    public String getCurUserName() {
        return curUserName;
    }

    private String curUserName;

    public SignInDlg() {
        super();

        controller().connect("captcha-image", "click", this::captchaImageClick);
        controller().connect("", "verify-error", MsgUtils::verifyError);

        updateData();
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

            this.curUserName = loginName;
            controller().disable("ok");

            new AuthenticationProxy().signIn(params)
                                     .thenApplyAsync(Result::booleanValue)
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

    private void updateData() {
        CompletableFuture.supplyAsync(() -> Application.settings().getProperty("last-signed-user"))
                         .thenApplyAsync(StringUtils::trimToEmpty)
                         .whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
    }

    private void updateDataCallback(
            String lastSignedUser,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (!lastSignedUser.isEmpty()) {
                controller().setText("login-name", lastSignedUser);
                focusInWindow(controller().get(JComponent.class, "password"));
            } else {
                focusInWindow(controller().get(JComponent.class, "login-name"));
            }
        }
    }

    private void updateCaptchaImage() {
        new AuthenticationProxy().captchaImage()
                                 .thenApplyAsync(ResultUtils::readImage)
                                 .whenCompleteAsync(this::updateCaptchaImageCallback, UPDATE_UI);
    }

    private void updateCaptchaImageCallback(
            Image image,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().get(ImageBox.class, "captcha-image").setData(image);
            pack();
        }
    }

    private void signInCallback(
            boolean result,
            Throwable throwable
    ) {
        if (throwable != null) {
            ErrorHandler.handle(throwable);
        } else {
            if (result) {
                super.done(OK);
            } else {
                fail(this.getOwner(), controller().getMessage("fail"));

                controller().setText("password", null);
                controller().setText("captcha-code", null);
                focusInWindow(controller().get(JComponent.class, "password"));
            }
        }

        controller().enable("ok");
    }
}
