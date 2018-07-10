package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static com.banhui.console.rpc.ResultUtils.dateValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditOrgAccountInfoDlg
        extends DialogPane {

    private volatile long id;
    private Map<String, Object> row;

    public EditOrgAccountInfoDlg(
            long id
    ) {
        this.id = id;
        if (this.id != 0) {
            setTitle(getTitle() + id);
        }
        updateData();

        controller().readOnly("login-name",true);
        controller().readOnly("mobile",true);
        controller().readOnly("create-time",true);
        controller().readOnly("update-time",true);
        controller().readOnly("update-time",true);
        controller().readOnly("lvl",true);
    }

    private void updateData() {
        assertUIThread();
        new AccountsProxy().queryAccOrgInfoById(id)
                           .thenApplyAsync(Result::map)
                           .thenAcceptAsync(this::updateDataCallback, UPDATE_UI)
                           .exceptionally(ErrorHandler::handle);

    }

    private void updateDataCallback(
            Map<String, Object> data
    ) {
        controller().setText("login-name", stringValue(data, "loginName"));
        controller().setText("real-name", stringValue(data, "realName"));
        controller().setText("position", stringValue(data, "position"));
        controller().setText("id-card", stringValue(data, "idCard"));
        controller().setText("mobile", stringValue(data, "mobile"));
        controller().setText("email", stringValue(data, "email"));
        controller().setText("qq-number", stringValue(data, "qqNumber"));
        controller().setText("company", stringValue(data, "company"));
        controller().setDate("create-time", dateValue(data, "createTime"));
        controller().setDate("update-time", dateValue(data, "updateTime"));
        controller().setInteger("lvl", intValue(data, "lvl"));
        controller().setText("org-name", stringValue(data, "orgName"));
        controller().setText("company-type", stringValue(data, "companyType"));
        controller().setText("address", stringValue(data, "address"));
        controller().setText("postal-code", stringValue(data, "postalCode"));
        controller().setText("buss-lic", stringValue(data, "bussLic"));
        controller().setText("org-code-no", stringValue(data, "orgCodeNo"));
        controller().setText("law-name", stringValue(data, "lawName"));
        controller().setText("law-id-card", stringValue(data, "lawIdCard"));
        controller().setText("home-phone", stringValue(data, "homePhone"));
        controller().setText("acc-bank", stringValue(data, "accBank"));
        controller().setText("account", stringValue(data, "account"));
        controller().setText("acc-user-name", stringValue(data, "accUserName"));
    }


    public Map<String, Object> getResultRow() {
        return this.row;
    }
}
