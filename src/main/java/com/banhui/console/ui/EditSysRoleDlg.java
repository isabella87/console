package com.banhui.console.ui;


import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.SysProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditSysRoleDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditSysRoleDlg.class);

    private final String name;

    private volatile Map<String, Object> resultObj;

    public EditSysRoleDlg(
            String name
    ) {
        this.name = trimToEmpty(name);
        this.resultObj = null;

        controller().readOnly("name", true);

        if (this.name.isEmpty()) {
            setTitle(controller().getMessage("create") + getTitle());
            controller().readOnly("name", false);
        } else {
            setTitle(controller().getMessage("edit") + getTitle() + name);
            updateData();
        }

        controller().connect("", "verify-error", MsgUtils::verifyError);
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();
            params.put("role-name", controller().getText("name").trim());
            params.put("title", controller().getText("title").trim());
            params.put("description", controller().getText("description").trim());
            params.put("enabled", controller().getBoolean("enabled"));

            (this.name.isEmpty() ? new SysProxy().addRole(params) : new SysProxy().updateRole(params))
                    .thenApplyAsync(Result::map)
                    .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    public final Map<String, Object> getResultObj() {
        return this.resultObj;
    }

    private void updateData() {
        new SysProxy().role(name)
                      .thenApplyAsync(Result::map)
                      .whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
    }

    private void updateDataCallback(
            Map<String, Object> data,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            // 帐户信息。
            controller().setText("name", stringValue(data, "name"));
            controller().setText("title", stringValue(data, "title"));
            controller().setText("description", stringValue(data, "description"));
            controller().setBoolean("enabled", booleanValue(data, "enabled"));

        }
    }

    private void saveCallback(
            Map<String, Object> row,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.resultObj = row;

            logger.trace("result: {}", row);

            super.done(OK);
        }

        controller().enable("ok");
    }
}
