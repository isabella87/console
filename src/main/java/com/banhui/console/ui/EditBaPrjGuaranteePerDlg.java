package com.banhui.console.ui;


import com.banhui.console.rpc.BaPrjGuaranteePersProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditBaPrjGuaranteePerDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditBaPrjGuaranteePerDlg.class);

    private volatile long id;
    private Map<String, Object> row;

    public EditBaPrjGuaranteePerDlg(
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
                params.put("bgp-id", id);
            }

            params.put("name", controller().getText("name").trim());
            params.put("show-name", controller().getText("show-name").trim());
            params.put("age", controller().getNumber("age"));
            params.put("show-age", controller().getText("show-age"));
            params.put("id-card", controller().getText("id-card"));
            params.put("gender", controller().getText("gender").trim());
            params.put("address", controller().getText("address").trim());
            params.put("show-address", controller().getText("show-address").trim());
            params.put("mobile", controller().getText("mobile").trim());
            params.put("postcode", controller().getText("postcode").trim());
            params.put("intro", controller().getText("intro").trim());

            (this.id == 0 ? new BaPrjGuaranteePersProxy().add(params) : new BaPrjGuaranteePersProxy().update(this.id, params))
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

        new BaPrjGuaranteePersProxy().query(this.id)
                                     .thenApplyAsync(Result::map)
                                     .thenAcceptAsync(this::updateDataCallback, UPDATE_UI)
                                     .exceptionally(ErrorHandler::handle);
    }

    @SuppressWarnings("unchecked")
    private void updateDataCallback(
            Map data
    ) {
        controller().setText("name", stringValue(data, "name"));
        controller().setText("show-name", stringValue(data, "showName"));
        controller().setInteger("age", intValue(data, "age"));
        controller().setInteger("show-age", intValue(data, "showAge"));
        controller().setText("id-card", stringValue(data, "idCard"));
        controller().setText("gender", stringValue(data, "gender"));
        controller().setText("address", stringValue(data, "address"));
        controller().setText("show-address", stringValue(data, "showAddress"));
        controller().setText("mobile", stringValue(data, "mobile"));
        controller().setText("postcode", stringValue(data, "postcode"));
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
