package com.banhui.console.ui;

import com.banhui.console.rpc.DisclosureProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditDisclosureDetailDlg extends DialogPane {
    private volatile long adrId;
    private volatile long role;

    public EditDisclosureDetailDlg(
            long adrId,
            long role
    ) {
        this.adrId = adrId;
        this.role = role;
        if (role == 0) {
            setTitle(controller().getMessage("create") + getTitle());
        } else {
            setTitle(controller().getMessage("edit") + getTitle());
            updateData();
        }
    }

    private void updateData() {
        assertUIThread();
        final Map<String, Object> params = new HashMap<>();
        params.put("adr-id", adrId);
        new DisclosureProxy().detail(params)
                             .thenApplyAsync(Result::map)
                             .whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            final Map<String, Object> params = new HashMap<>();
            params.put("adr-id", adrId);
            StringBuilder sb = new StringBuilder();
            sb = itemString(101, 143, sb);
            sb = itemString(201, 214, sb);
            sb = itemString(301, 305, sb);
            String str = sb.toString();
            if (str.isEmpty()) {
                warn(null, controller().getMessage("detail-null"));
                return;
            }
            controller().disable("ok");
            str = str.substring(0, str.lastIndexOf(","));
            params.put("items", str);
            (this.role == 0 ? new DisclosureProxy().createDetail(params) : new DisclosureProxy().updateDetail(params))
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
            super.done(OK);
        }
    }

    private void updateDataCallback(
            Map<String, Object> data,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
            controller().enable("ok");
        } else {
            for (String key : data.keySet()) {
                try {
                    controller().setText(key, stringValue(data, key));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public StringBuilder itemString(
            int a,
            int b,
            StringBuilder sb
    ) {
        for (int i = a; i <= b; i++) {
            String key = String.valueOf(i);
            String value = controller().getText(key);
            if (!value.isEmpty()) {
                sb.append(key).append("=").append(value).append(",");
            }
        }
        return sb;
    }

}
