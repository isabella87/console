package com.banhui.console.ui;


import com.banhui.console.rpc.BaPrjCtrosProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class EditBaPrjCtorDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditBaPrjCtorDlg.class);

    private volatile long id;
    private Map<String, Object> row;

    public EditBaPrjCtorDlg(
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
                params.put("bco-id", id);
            }

            params.put("name", controller().getText("name").trim());
            params.put("show-name", controller().getText("show-name").trim());
            params.put("ent-nature", controller().getText("ent-nature"));
            params.put("ent-quality", controller().getText("ent-quality"));
            params.put("ent-strength", controller().getText("ent-strength").trim());
            params.put("registered-date", controller().getDate("registered-date"));
            params.put("reg-years", controller().getText("reg-years").trim());
            params.put("show-reg-years", controller().getText("show-reg-years").trim());
            params.put("reg-funds", controller().getText("reg-funds").trim());
            params.put("show-reg-funds", controller().getText("show-reg-funds").trim());
            params.put("lasted-area", controller().getText("lasted-area").trim());
            params.put("lasted-output", controller().getText("lasted-output").trim());
            params.put("qualification", controller().getText("qualification").trim());
            params.put("nation-prize-count", controller().getNumber("nation-prize-count"));
            params.put("provin-prize-count", controller().getNumber("provin-prize-count"));
            params.put("intro", controller().getText("intro").trim());

            (this.id == 0 ? new BaPrjCtrosProxy().add(params) : new BaPrjCtrosProxy().update(this.id, params))
                    .thenApplyAsync(Result::map)
                    .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                    .exceptionally(MsgBox::showError)
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

        new BaPrjCtrosProxy().query(this.id)
                             .thenApplyAsync(Result::map)
                             .thenAcceptAsync(this::updateDataCallback, UPDATE_UI)
                             .exceptionally(MsgBox::showError);
    }

    @SuppressWarnings("unchecked")
    private void updateDataCallback(
            Map data
    ) {
        controller().setText("name", stringValue(data, "name"));
        controller().setText("show-name", stringValue(data, "showName"));
        controller().setText("ent-nature", stringValue(data, "entNature"));
        controller().setText("ent-quality", stringValue(data, "entQuality"));
        controller().setText("ent-strength", stringValue(data, "entStrength"));
        controller().setDate("registered-date", dateValue(data, "registeredDate"));
        controller().setText("reg-years", stringValue(data, "regYears"));
        controller().setText("show-reg-years", stringValue(data, "showRegYears"));
        controller().setText("reg-funds", stringValue(data, "regFunds"));
        controller().setText("show-reg-funds", stringValue(data, "showRegFunds"));
        controller().setText("lasted-area", stringValue(data, "lastedArea"));
        controller().setText("lasted-output", stringValue(data, "lastedOutput"));
        controller().setText("qualification", stringValue(data, "qualification"));
        controller().setInteger("nation-prize-count", intValue(data, "nationPrizeCount"));
        controller().setInteger("provin-prize-count", intValue(data, "provinPrizeCount"));
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
