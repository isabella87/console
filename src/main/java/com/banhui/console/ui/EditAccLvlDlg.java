package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.DialogUtils.prompt;

public class EditAccLvlDlg
        extends DialogPane {
    private volatile long auId;
    private volatile int lev;

    public EditAccLvlDlg(
            long auId,
            int lvl
    ) {
        this.auId = auId;
        if (this.auId != 0) {
            setTitle(getTitle() + auId);
        }
        controller().setInteger("lev", lvl);
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
            final int lev = controller().getInteger("lev");
            params.put("lev", lev);
            new AccountsProxy().updateLevel(params)
                               .thenApplyAsync(Result::longValue)
                               .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                               .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Long a,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (a > 0) {
                prompt(null, controller().getMessage("edit-success"));
                this.lev = controller().getInteger("lev");
                super.done(OK);
            } else {
                prompt(null, controller().getMessage("edit-fail"));
            }
        }
    }

    public int getLev() {
        return lev;
    }
}
