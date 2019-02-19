package com.banhui.console.ui;

import com.banhui.console.rpc.BaPrjEngineersProxy;
import com.banhui.console.rpc.BaPrjMortgageProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditBaPrjMortgageDlg
        extends DialogPane {

    private volatile long id;
    private Map<String, Object> row;

    public EditBaPrjMortgageDlg(
            long id
    ) {
        this.id = id;
        if (this.id == 0) {
            setTitle(controller().getMessage("create") + getTitle());
        } else {
            setTitle(controller().getMessage("edit") + getTitle());
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

            if (this.id != 0) {
                params.put("bpm-id", id);
            }
            params.put("m-type", controller().getNumber("m-type"));
            params.put("content", controller().getText("content").trim());
            params.put("evaluation", controller().getDecimal("evaluation"));
            params.put("owner-name", controller().getText("owner-name").trim());
            params.put("owner-show-name", controller().getText("owner-show-name").trim());
            params.put("owner-id-no", controller().getText("owner-id-no").trim());
            params.put("link-man", controller().getText("link-man").trim());
            params.put("link-mobile", controller().getText("link-mobile").trim());
            params.put("link-address", controller().getText("link-address").trim());
            params.put("comment", controller().getText("remark").trim());

            (this.id == 0 ? new BaPrjMortgageProxy().add(params) : new BaPrjMortgageProxy().update(this.id, params))
                    .thenApplyAsync(Result::map)
                    .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                    .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
        } else {
            super.done(result);
        }
    }

    public final long getId() {
        return this.id;
    }

    private void updateData() {
        assertUIThread();
        new BaPrjMortgageProxy().query(this.id)
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
            controller().setNumber("m-type", longValue(data, "mType"));
            controller().setText("content", stringValue(data, "content"));
            controller().setDecimal("evaluation", decimalValue(data, "evaluation"));
            controller().setText("owner-name", stringValue(data, "ownerName"));
            controller().setText("owner-show-name", stringValue(data, "ownerShowName"));
            controller().setText("owner-id-no", stringValue(data, "ownerIdNo"));
            controller().setText("link-man", stringValue(data, "linkMan"));
            controller().setText("link-mobile", stringValue(data, "linkMobile"));
            controller().setText("link-address", stringValue(data, "linkAddress"));
            controller().setText("remark", stringValue(data, "remark"));
        }
    }

    private void saveCallback(
            Map<String, Object> row,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.row = row;
            super.done(OK);
        }
    }

    public Map<String, Object> getResultRow() {
        return this.row;
    }
}
