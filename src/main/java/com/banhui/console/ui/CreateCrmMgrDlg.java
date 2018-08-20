package com.banhui.console.ui;


import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CreateCrmMgrDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(CreateCrmMgrDlg.class);

    private Long id;

    private String uName;

    @Override
    public void done(int result) {
        if (result == OK) {
            Map<String, Object> params = new HashMap<>();

            params.put("p-name", controller().getText("p-name"));
            params.put("u-name", controller().getText("u-name"));
            params.put("position", controller().getText("position"));
            params.put("r-code", controller().getText("r-code"));

            new CrmProxy().createCrmMgrRelation(params)
                          .thenApplyAsync(Result::longValue)
                          .whenCompleteAsync(this::createCallback, UPDATE_UI);
        } else {
            super.done(CANCEL);
        }
    }

    private void createCallback(
            Long result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.id = result;
            this.uName = controller().getText("u-name");
            super.done(OK);
        }
    }

    public final long getId() {
        return this.id;
    }

    public String getuName() {
        return this.uName;
    }
}
