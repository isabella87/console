package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.DialogUtils.fail;

public class ConfirmRegistryDlg
        extends DialogPane {
    private volatile long auId;

    public ConfirmRegistryDlg(
            long id
    ) {
        this.auId = id;
        if (auId != 0) {
            setTitle(getTitle() + auId);
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");
            final Map<String, Object> params = new HashMap<>();
            if (auId != 0) {
                params.put("au-id", auId);
            }
            String userId = controller().getText("user-id");
            params.put("user-id", userId);
            String confirmRegistryText = controller().getMessage("confirm-registry-text");
            if (confirm(this.getOwner(), confirmRegistryText, "开户")) {
                new AccountsProxy().confirmRegistry(params)
                                   .thenApplyAsync(Result::booleanValue)
                                   .whenCompleteAsync(this::saveCallback, UPDATE_UI);
            } else {
                close();
            }
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            warn(null, t.getCause().getMessage());
        } else {
            if (!flag) {
                fail(null, controller().getMessage("registry-fail"));
            } else {
                prompt(null, controller().getMessage("registry-success"));
            }
        }
        super.done(OK);
    }
}

