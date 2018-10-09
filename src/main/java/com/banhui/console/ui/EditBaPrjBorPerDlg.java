package com.banhui.console.ui;


import com.banhui.console.rpc.BaPrjBorPersProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditBaPrjBorPerDlg
        extends DialogPane {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(EditBaPrjBorPerDlg.class);

    private volatile long id;
    private Map<String, Object> row;

    public EditBaPrjBorPerDlg(
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
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();

            if (this.id != 0) {
                params.put("bpmp-id", id);
            }

            params.put("real-name", controller().getText("real-name").trim());
            params.put("show-name", controller().getText("show-name").trim());
            params.put("id-card", controller().getText("id-card").trim());
            params.put("mobile", controller().getText("mobile").trim());
            params.put("email", controller().getText("email").trim());
            params.put("address", controller().getText("address").trim());
            params.put("work-years", controller().getNumber("work-years"));
            params.put("intro", controller().getText("intro").trim());
            params.put("company", controller().getText("company").trim());
            params.put("position", controller().getText("position").trim());
            params.put("age", controller().getText("age").trim());
            params.put("id-card-address-prov", controller().getText("id-card-address-prov").trim());
            params.put("id-card-address-city", controller().getText("id-card-address-city").trim());
            params.put("show-age", controller().getText("show-age").trim());
            params.put("gender", controller().getText("gender").trim());
            params.put("wchat", controller().getText("wchat").trim());
            params.put("linkman-name", controller().getText("linkman-name").trim());
            params.put("linkman-id-card", controller().getText("linkman-id-card").trim());
            params.put("linkman-mobile", controller().getText("linkman-mobile").trim());
            params.put("linkman-fax", controller().getText("linkman-fax").trim());
            params.put("linkman-email", controller().getText("linkman-email").trim());
            params.put("linkman-wchat", controller().getText("linkman-wchat").trim());
            params.put("linkman-address", controller().getText("linkman-address").trim());
            params.put("fax", controller().getText("fax").trim());
            params.put("linkman-show-address", controller().getText("linkman-show-address").trim());
            params.put("linkman-show-name", controller().getText("linkman-show-name").trim());
            params.put("show-address", controller().getText("show-address").trim());
            params.put("industry", controller().getText("industry").trim());
            params.put("work-nature", controller().getText("work-nature").trim());
            params.put("other-info", controller().getText("other-info").trim());

            (this.id == 0 ? new BaPrjBorPersProxy().add(params) : new BaPrjBorPersProxy().update(this.id, params))
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

        new BaPrjBorPersProxy().query(this.id)
                               .thenApplyAsync(Result::map)
                               .thenAcceptAsync(this::updateDataCallback, UPDATE_UI)
                               .exceptionally(ErrorHandler::handle);
    }

    private void updateDataCallback(
            Map<String, Object> data
    ) {
        controller().setText("real-name", stringValue(data, "realName"));
        controller().setText("show-name", stringValue(data, "showName"));
        controller().setText("id-card", stringValue(data, "idCard"));
        controller().setText("mobile", stringValue(data, "mobile"));
        controller().setText("email", stringValue(data, "email"));
        controller().setText("address", stringValue(data, "address"));
        controller().setNumber("work-years", longValue(data, "workYears"));
        controller().setText("intro", stringValue(data, "intro"));
        controller().setText("company", stringValue(data, "company"));
        controller().setText("position", stringValue(data, "position"));
        controller().setNumber("age", longValue(data, "age"));
        controller().setText("id-card-address-prov", stringValue(data, "idCardAddressProv"));
        controller().setText("id-card-address-city", stringValue(data, "idCardAddressCity"));
        controller().setText("show-age", stringValue(data, "showAge"));
        controller().setText("gender", stringValue(data, "gender"));
        controller().setText("wchat", stringValue(data, "wchat"));
        controller().setText("linkman-name", stringValue(data, "linkmanName"));
        controller().setText("linkman-id-card", stringValue(data, "linkmanIdCard"));
        controller().setText("linkman-mobile", stringValue(data, "linkmanMobile"));
        controller().setText("linkman-fax", stringValue(data, "linkmanFax"));
        controller().setText("linkman-email", stringValue(data, "linkmanEmail"));
        controller().setText("linkman-wchat", stringValue(data, "linkmanWchat"));
        controller().setText("linkman-address", stringValue(data, "linkmanAddress"));
        controller().setText("fax", stringValue(data, "fax"));
        controller().setText("linkman-show-address", stringValue(data, "linkmanShowAddress"));
        controller().setText("linkman-show-name", stringValue(data, "linkmanShowName"));
        controller().setText("show-address", stringValue(data, "showAddress"));
        controller().setText("industry", stringValue(data, "industry"));
        controller().setText("work-nature", stringValue(data, "workNature"));
        controller().setText("other-info", stringValue(data, "otherInfo"));

    }

    private void saveCallback(
            Map<String, Object> row,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
            controller().enable("ok");
        } else {
            this.row = row;
            super.done(OK);
        }
    }

    public Map<String, Object> getResultRow() {
        return this.row;
    }
}
