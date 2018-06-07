package com.banhui.console.ui;

import com.banhui.console.rpc.RpcException;
import org.xx.armory.swing.UIControllers;
import org.xx.armory.swing.components.ErrorDialog;

public class RpcErrorDlg
        extends ErrorDialog<RpcException> {
    public RpcErrorDlg(
            RpcException throwable
    ) {
        super(throwable);

        controller().hide("copy-more");
        controller().hide("show-more");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMessage() {
        switch (getThrowable().getStatusCode()) {
            case 403:
                return UIControllers.GLOBAL.getMessage("error-rpc-access-denied");
            case 404:
                return UIControllers.GLOBAL.getMessage("error-rpc-not-found");
            case 500:
                final String content = getThrowable().getMessage();
                if (content.isEmpty()) {
                    return UIControllers.GLOBAL.getMessage("error-rpc-server-internal");
                } else {
                    final int p1 = content.indexOf('*');
                    return content.substring(p1 + 1);
                }
            default:
                return super.getMessage();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDetail() {
        return UIControllers.GLOBAL.formatMessage("error-rpc-detail", getThrowable().getMethod(), getThrowable().getUri());
    }
}
