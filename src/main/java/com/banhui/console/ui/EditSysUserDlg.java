package com.banhui.console.ui;


import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.SysProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ListItem;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static com.banhui.console.rpc.ResultUtils.stringValues;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.xx.armory.swing.ComponentUtils.updateListBox;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditSysUserDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditSysUserDlg.class);

    private final String userName;

    private volatile Map<String, Object> resultObj;

    public EditSysUserDlg(
            String userName
    ) {
        this.userName = trimToEmpty(userName);
        this.resultObj = null;

        controller().readOnly("user-name", true);
        controller().readOnly("create-date", true);

        if (this.userName.isEmpty()) {
            setTitle(controller().getMessage("create") + getTitle());
            controller().readOnly("user-name", false);
        } else {
            setTitle(controller().getMessage("edit") + getTitle());
        }

        updateData();
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();
            params.put("user-name", controller().getText("user-name").trim());
            params.put("mobile", controller().getText("mobile").trim());
            params.put("email", controller().getText("email").trim());
            params.put("enabled", controller().getBoolean("enabled"));
            params.put("roles", controller().getText("roles").trim());

            (this.userName.isEmpty() ? new SysProxy().add(params) : new SysProxy().update(params))
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
        allDone(
                new SysProxy().allRoles(null),
                new SysProxy().userByName(this.userName),
                new SysProxy().rolesByUserName(this.userName)
        ).thenApplyAsync(results -> new Object[]{
                results[0].list(),
                results[1].map(),
                results[2].array()
        }).whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void updateDataCallback(
            Object[] data,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            // 所有角色列表。
            final List<Map<String, Object>> allRoles = (List<Map<String, Object>>) data[0];
            final JList<ListItem> roles = controller().get(JList.class, "roles");
            updateListBox(roles, allRoles.stream()
                                         .map(m -> new ListItem(m.get("title").toString(), m.get("name")))
                                         .toArray(ListItem[]::new));

            // 帐户信息。
            final Map<String, Object> user = (Map<String, Object>) data[1];
            controller().setText("user-name", stringValue(user, "userName"));
            controller().setText("mobile", stringValue(user, "mobile"));
            controller().setText("email", stringValue(user, "email"));
            controller().setBoolean("enabled", booleanValue(user, "enabled"));
            controller().setDate("create-date", dateValue(user, "createDate"));

            // 帐户关联角色列表。
            final String[] userRoles = stringValues((Object[]) data[2]);
            controller().setValues("roles", userRoles);
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
