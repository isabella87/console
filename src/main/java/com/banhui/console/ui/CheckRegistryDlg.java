package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CheckRegistryDlg
        extends DialogPane {
    private volatile long auId;

    public CheckRegistryDlg(
            long auId,
            String idCard
    ) {
        this.auId = auId;
        if (this.auId != 0) {
            setTitle(getTitle() + auId);
        }
        controller().setText("id-card", idCard);
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
            String idCard = controller().getText("id-card");
            params.put("id-card", idCard);
            new AccountsProxy().checkRegistry(params)
                               .thenApplyAsync(Result::booleanValue)
                               .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                               .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Boolean flag,
            Throwable t
    ) {
        close();
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (!flag) {
                fail(null, controller().getMessage("check-fail"));
            } else {
                prompt(null, controller().getMessage("check-success"));
            }
        }
        super.done(OK);
    }
}
