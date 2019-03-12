package com.banhui.console.ui;

import com.banhui.console.rpc.DisclosureProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditInfoDisclosureDlg
        extends DialogPane {

    private volatile long adrId;

    public EditInfoDisclosureDlg(
            long adrId
    ) {
        this.adrId = adrId;
        if (this.adrId == 0) {
            setTitle(controller().getMessage("create") + getTitle());
        } else {
            setTitle(controller().getMessage("edit") + getTitle());
            updateData();
        }
        controller().readOnly("name", true);
    }

    private void updateData() {
        assertUIThread();
        final Map<String, Object> params = new HashMap<>();
        params.put("adr-id", adrId);
        new DisclosureProxy().infoById(params)
                             .thenApplyAsync(Result::map)
                             .whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            final Map<String, Object> params = new HashMap<>();
            if (this.adrId != 0) {
                params.put("adr-id", adrId);
            }
            long type = controller().getNumber("type");
            Long month = controller().getNumber("month");
            params.put("year", controller().getNumber("year"));
            params.put("month", month);
            params.put("type", type);
            if (type == 0 && null == month) {
                warn(null, controller().getMessage("month-null"));
                return;
            }
            controller().disable("ok");
            (this.adrId == 0 ? new DisclosureProxy().createOverview(params) : new DisclosureProxy().updateOverviews(params))
                    .thenApplyAsync(Result::booleanValue)
                    .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        } else {
            super.done(result);
        }
    }


    private void updateDataCallback(
            Map<String, Object> data,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().setText("year", stringValue(data, "year").trim());
            controller().setText("month", stringValue(data, "month").trim());
            controller().setNumber("type", longValue(data, "type"));
            controller().setText("name", stringValue(data, "name").trim());
        }
    }


    private void saveCallback(
            boolean rets,
            Throwable t
    ) {
        controller().enable("ok");
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            super.done(OK);
        }
    }

}
