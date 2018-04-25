package com.banhui.console.ui;


import com.banhui.console.rpc.AuthenticationProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ImageBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.UPDATE_UI;

/**
 * 登录对话框。
 */
public class SignInDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(SignInDlg.class);

    public SignInDlg() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initUi() {
        super.initUi();

        new AuthenticationProxy().captchaImage()
                                 .thenApplyAsync(this::readImage)
                                 .thenAcceptAsync(this::updateCaptchaImage, UPDATE_UI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            final String loginName = getUiController().getText("login-name").trim();
            final String password = getUiController().getText("password").trim();
            final String captchaCode = getUiController().getText("captcha-code").trim();

            if (loginName.isEmpty() || password.isEmpty() || captchaCode.isEmpty()) {
                return;
            }

            final Map<String, Object> params = new HashMap<>();
            params.put("user-name", loginName);
            params.put("password", password);
            params.put("captcha-code", captchaCode);

            getUiController().disable("ok");

            /**
             * 一，界面的编写及事件处理规则
             * 1，在resources目录下编写  *.rc 文件，并排版界面；（该rc文件的名称与后期编写的界面初始化及事件处理的java类的文件名要相同，是根据同名找到对应的界面）
             * 2，调用该方法initUi初始化界面，如果有特殊操作重写该方法；
             * 3，调用done方法处理响应事件，该方法默认处理结果是关闭当前窗口。如果有特殊操作或者需要弹出其他界面，则重写该方法。
             *
             * 二，一般任务分四步，三个线程走完。
             * 1,连接服务的http线程，比如此处的触发signIn方法；
             * 2，拦截异常并处理异常的公共线程；
             * 3，处理异步回调数据的公共线程；
             * 4，根据异步回调结果数据更新界面的UI线程。
             */
            new AuthenticationProxy().signIn(params)
                                     //.exceptionally()
                                     .thenApplyAsync(r -> r.booleanValue().orElse(false))
                                     .thenAcceptAsync(this::signInCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private BufferedImage readImage(
            byte[] data
    ) {
        try {
            return ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException ex) {
            logger.warn("cannot read image from bytes", ex);
            return null;
        }
    }

    private void updateCaptchaImage(
            BufferedImage image
    ) {
        getUiController().get(ImageBox.class, "captcha-image").setData(image);
        layoutOwner();
    }

    private void signInCallback(
            boolean result
    ) {
        if (result) {
            super.done(OK);
        } else {
            getUiController().setText("password", null);
            getUiController().setText("captcha-code", null);
            getUiController().get(JComponent.class, "password").requestFocusInWindow();
            getUiController().enable("ok");
        }
    }
}
