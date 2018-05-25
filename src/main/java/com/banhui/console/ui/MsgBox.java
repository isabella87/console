package com.banhui.console.ui;

import com.banhui.console.rpc.RpcException;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.UIUtils;
import org.xx.armory.swing.components.ConfirmDialog;
import org.xx.armory.swing.components.ConfirmInputDialog;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ErrorDialog;
import org.xx.armory.swing.components.LineInputDialog;
import org.xx.armory.swing.components.LinesInputDialog;
import org.xx.armory.swing.components.PopupDialog;

import javax.swing.*;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.join;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.unwrapThrowable;

/**
 * 显示所有消息对话框的工具类。
 */
public final class MsgBox {
    private MsgBox() {
    }

    /**
     * 显示确认对话框。
     *
     * @param message
     *         需要确认的消息。
     * @return 是否确认。
     */
    public static boolean showConfirm(
            String message
    ) {
        return showConfirm(message, null);
    }

    /**
     * 显示双重确认对话框。
     *
     * <p>第一次确认后，还需要输入指定的值进行第二步确认。</p>
     *
     * @param message
     *         需要确认的消息。
     * @param expectedValue
     *         第二步确认时需要输入的值，如果此参数是{@code null}或者是空字符串则不进行第二步确认。
     * @return 是否确认。
     */
    public static boolean showConfirm(
            String message,
            String expectedValue
    ) {
        final ConfirmDialog dlg = new ConfirmDialog(message);
        boolean result = showModel(Application.mainFrame(), dlg) == DialogPane.YES;

        if (result && expectedValue != null && !expectedValue.isEmpty()) {
            final ConfirmInputDialog dlg2 = new ConfirmInputDialog(expectedValue);
            return showModel(Application.mainFrame(), dlg2) == DialogPane.OK;
        } else {
            return false;
        }
    }

    /**
     * 显示错误对话框。
     *
     * @param throwable
     *         待显示的错误。
     * @return {@code null}。
     */
    public static Void showError(
            Throwable throwable
    ) {
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

            showModel(Application.mainFrame(), dlg);
        } else {
            UIUtils.defaultDumpThrowable(cause);
        }

        return null;
    }

    public static void popupPrompt(
            String message
    ) {
        showModel(Application.mainFrame(), new PopupDialog(PopupDialog.PROMPT, message));
    }

    public static void popupWarn(
            String message
    ) {
        showModel(Application.mainFrame(), new PopupDialog(PopupDialog.WARN, message));
    }

    public static void popupFail(
            String message
    ) {
        showModel(Application.mainFrame(), new PopupDialog(PopupDialog.FAIL, message));
    }

    public static String showInputText(
            String prompt,
            String defaultValue
    ) {
        final LineInputDialog dlg = new LineInputDialog(prompt, LineInputDialog.TEXT, defaultValue);
        showModel(Application.mainFrame(), dlg);
        return dlg.getTextResult();
    }

    public static String showInputMultiLineText(
            String prompt,
            String... defaultValue
    ) {
        final String text = defaultValue != null ? join(defaultValue, "\n") : "";
        final LinesInputDialog dlg = new LinesInputDialog(prompt, text);
        showModel(Application.mainFrame(), dlg);
        return dlg.getTextResult();
    }

    public static String showInputPassword(
            String prompt,
            String defaultValue
    ) {
        final LineInputDialog dlg = new LineInputDialog(prompt, LineInputDialog.PASSWORD, defaultValue);
        showModel(Application.mainFrame(), dlg);
        return dlg.getPasswordResult();
    }

    public static Date showInputDate(
            String prompt,
            Date defaultValue
    ) {
        final LineInputDialog dlg = new LineInputDialog(prompt, LineInputDialog.DATE, defaultValue);
        showModel(Application.mainFrame(), dlg);
        return dlg.getDateResult();
    }
}
