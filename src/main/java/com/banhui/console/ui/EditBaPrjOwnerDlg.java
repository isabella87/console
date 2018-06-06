package com.banhui.console.ui;


import com.banhui.console.rpc.BaPrjOwnersProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditBaPrjOwnerDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditBaPrjOwnerDlg.class);

    private volatile long id;
    private Map<String, Object> row;

    public EditBaPrjOwnerDlg(
            long id
    ) {
        this.id = id;

        if (this.id == 0) {
            setTitle(controller().getMessage("create") + getTitle());
        } else {
            updateData();
            setTitle(controller().getMessage("edit") + getTitle());
        }
    }

    @Override
    protected void initUi() {
        super.initUi();

    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();

            if (this.id != 0) {
                params.put("bo-id", id);
            }

            params.put("owner-name", controller().getText("owner-name").trim());
            params.put("owner-show-name", controller().getText("show-owner-name").trim());
            params.put("registered-date", controller().getDate("registered-date"));
            params.put("reg-years", controller().getNumber("reg-years"));
            params.put("reg-funds", controller().getDecimal("reg-funds"));
            params.put("show-reg-funds", controller().getText("show-reg-funds").trim());
            params.put("ent-industry", controller().getText("ent-industry").trim());
            params.put("owner-nature", controller().getText("owner-nature").trim());
            params.put("owner-strength", controller().getText("owner-strength").trim());
            params.put("owner-quality", controller().getText("owner-quality").trim());
            params.put("intro", controller().getText("intro").trim());

            (this.id == 0 ? new BaPrjOwnersProxy().add(params) : new BaPrjOwnersProxy().update(this.id, params))
                    .thenApplyAsync(Result::map)
                    .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                    .exceptionally(ErrorHandler::handle)
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

        new BaPrjOwnersProxy().query(this.id)
                              .thenApplyAsync(Result::map)
                              .thenAcceptAsync(this::updateDataCallback, UPDATE_UI)
                              .exceptionally(ErrorHandler::handle);
    }

    @SuppressWarnings("unchecked")
    private void updateDataCallback(
            Map data
    ) {
        controller().setText("owner-name", stringValue(data, "ownerName"));
        controller().setText("show-owner-name", stringValue(data, "ownerShowName"));
        controller().setDate("registered-date", dateValue(data, "registeredDate"));
        controller().setInteger("reg-years", intValue(data, "regYears"));
        controller().setDecimal("reg-funds", decimalValue(data, "regFunds"));
        controller().setText("show-reg-funds", stringValue(data, "showRegFunds"));
        controller().setText("ent-industry", stringValue(data, "entIndustry"));
        controller().setText("owner-nature", stringValue(data, "ownerNature"));
        controller().setText("owner-strength", stringValue(data, "ownerStrength"));
        controller().setText("owner-quality", stringValue(data, "ownerQuality"));
        controller().setText("intro", stringValue(data, "intro"));


    }

    private void saveCallback(
            Map<String, Object> row
    ) {

        this.row = row;

        super.done(OK);
    }

    public Map<String, Object> getResultRow() {
        return this.row;
    }
}
