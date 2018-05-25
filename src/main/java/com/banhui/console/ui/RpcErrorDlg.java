package com.banhui.console.ui;

import com.banhui.console.rpc.RpcException;
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
                return "没有足够的权限，请重新登录或者请管理员分配更多的权限。";
            case 404:
                return "找不到服务。";
            case 500:
                final String content = getThrowable().getMessage();
                if (content.isEmpty()) {
                    return "服务端内部错误";
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
        return "访问方法：" + getThrowable().getMethod() + "\n访问地址：" + getThrowable().getUri();
    }
}
