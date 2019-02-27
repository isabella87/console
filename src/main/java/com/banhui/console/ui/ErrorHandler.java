package com.banhui.console.ui;

import com.banhui.console.rpc.RpcException;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.UIUtils;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ErrorDialog;

import javax.swing.*;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.unwrapThrowable;

/**
 * 显示所有消息对话框的工具类。
 */
public final class ErrorHandler {
    private ErrorHandler() {
    }

    /**
     * 显示错误对话框。
     *
     * @param throwable
     *         待显示的错误。
     * @return {@code null}。
     */
    public static Void handle(
            Throwable throwable
    ) {
        if (throwable == null) {
            return null;
        }

        // 对于Error，继续抛出。
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        // 从包装异常中找到引起此异常的源。
        Throwable cause = unwrapThrowable(throwable);

        if (SwingUtilities.isEventDispatchThread()) {
            final DialogPane dlg;
            if (cause instanceof RpcException) {
                dlg = new RpcErrorDlg((RpcException) cause);
            } else {
                dlg = new ErrorDialog<>(cause);
            }

            showModel(null, dlg);
        } else {
            UIUtils.defaultDumpThrowable(cause);
        }

        return null;
    }
}
