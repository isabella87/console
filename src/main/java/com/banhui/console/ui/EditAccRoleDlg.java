package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditAccRoleDlg extends DialogPane {

    private volatile Long auId;
    private volatile Boolean allowInvest;
    private volatile Boolean allowBorrow;
    private volatile Boolean allowCommutate;
    private volatile Map<String, Object> rollParams;

    public EditAccRoleDlg(
            Map<String, Object> params
    ) {
        this.auId = longValue(params, "auId");
        this.allowInvest = booleanValue(params, "allowInvest");
        this.allowBorrow = booleanValue(params, "allowBorrow");
        this.allowCommutate = booleanValue(params, "allowCommutate");
        updateCheckBox();
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");
            final Map<String, Object> params = new HashMap<>();
            if (this.auId != 0) {
                params.put("au-id", this.auId);
            }
            params.put("allow-invest", controller().getBoolean("invest"));
            params.put("allow-borrow", controller().getBoolean("borrow"));
            params.put("allow-commutate", controller().getBoolean("commutate"));
            new AccountsProxy().setAllowRoles(params)
                               .thenApplyAsync(Result::booleanValue)
                               .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (flag) {
                rollParams = new HashMap<>();
                rollParams.put("allowInvest", controller().getBoolean("invest"));
                rollParams.put("allowBorrow", controller().getBoolean("borrow"));
                rollParams.put("allowCommutate", controller().getBoolean("commutate"));
                super.done(OK);
            }
        }
    }

    private void updateCheckBox() {
        if (allowInvest) {
            controller().setBoolean("invest", true);
        }
        if (allowBorrow) {
            controller().setBoolean("borrow", true);
        }
        if (allowCommutate) {
            controller().setBoolean("commutate", true);
        }
    }

    public Map<String, Object> getRollParams() {
        return rollParams;
    }
}
