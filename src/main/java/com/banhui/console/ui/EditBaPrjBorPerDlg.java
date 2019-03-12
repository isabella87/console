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
            params.put("intro", controller().getText("intro").trim());
            params.put("company", controller().getText("company").trim());
            params.put("position", controller().getText("position").trim());
            params.put("id-card-address-prov", controller().getText("id-card-address-prov").trim());
            //params.put("id-card-address-city", controller().getText("id-card-address-city").trim());
            params.put("wchat", controller().getText("wchat").trim());
            params.put("fax", controller().getText("fax").trim());
            params.put("qq", controller().getText("qq").trim());
            params.put("show-address", controller().getText("show-address").trim());
            params.put("work-nature", controller().getText("work-nature").trim());
            params.put("other-info", controller().getText("other-info").trim());
            for (int i = 1; i <= 6; i++) {
                String num = "";
                if (i != 1) {
                    num = String.valueOf(i);
                }
                params.put("linkman-name" + num, controller().getText("linkman-name" + num).trim());
                params.put("linkman-show-name" + num, controller().getText("linkman-show-name" + num).trim());
                params.put("linkman-mobile" + num, controller().getText("linkman-mobile" + num).trim());
                params.put("linkman-email" + num, controller().getText("linkman-email" + num).trim());
                params.put("linkman-wchat" + num, controller().getText("linkman-wchat" + num).trim());
                params.put("linkman-fax" + num, controller().getText("linkman-fax" + num).trim());
                params.put("linkman-address" + num, controller().getText("linkman-address" + num).trim());
                params.put("linkman-show-address" + num, controller().getText("linkman-show-address" + num).trim());
                params.put("linkman-id-card" + num, controller().getText("linkman-id-card" + num).trim());
                params.put("linkman-relation" + num, controller().getText("linkman-relation" + num).trim());
                params.put("linkman-relation" + num, controller().getText("linkman-relation" + num).trim());
                params.put("linkman-qq" + num, controller().getText("linkman-qq" + num).trim());
            }
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
                               .whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
    }

    private void updateDataCallback(
            Map<String, Object> data,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().setText("real-name", stringValue(data, "realName"));
            controller().setText("show-name", stringValue(data, "showName"));
            controller().setText("id-card", stringValue(data, "idCard"));
            controller().setText("mobile", stringValue(data, "mobile"));
            controller().setText("email", stringValue(data, "email"));
            controller().setText("address", stringValue(data, "address"));
            controller().setText("intro", stringValue(data, "intro"));
            controller().setText("company", stringValue(data, "company"));
            controller().setText("position", stringValue(data, "position"));
            String city = stringValue(data, "idCardAddressProv");
            if (city.isEmpty()) {
                controller().setText("id-card-address-prov", stringValue(data, "idCardAddressCity"));
            } else {
                controller().setText("id-card-address-prov", stringValue(data, "idCardAddressProv"));
            }
            controller().setText("wchat", stringValue(data, "wchat"));
            controller().setText("qq", stringValue(data, "qq"));
            controller().setText("fax", stringValue(data, "fax"));
            controller().setText("show-address", stringValue(data, "showAddress"));
            controller().setText("work-nature", stringValue(data, "workNature"));
            controller().setText("other-info", stringValue(data, "otherInfo"));
            for (int i = 1; i <= 6; i++) {
                String num = "";
                if (i != 1) {
                    num = String.valueOf(i);
                }
                controller().setText("linkman-name" + num, stringValue(data, "linkmanName" + num));
                controller().setText("linkman-show-name" + num, stringValue(data, "linkmanShowName" + num));
                controller().setText("linkman-mobile" + num, stringValue(data, "linkmanMobile" + num));
                controller().setText("linkman-email" + num, stringValue(data, "linkmanEmail" + num));
                controller().setText("linkman-wchat" + num, stringValue(data, "linkmanWchat" + num));
                controller().setText("linkman-fax" + num, stringValue(data, "linkmanFax" + num));
                controller().setText("linkman-address" + num, stringValue(data, "linkmanAddress" + num));
                controller().setText("linkman-show-address" + num, stringValue(data, "linkmanShowAddress" + num));
                controller().setText("linkman-id-card" + num, stringValue(data, "linkmanIdCard" + num));
                controller().setText("linkman-relation" + num, stringValue(data, "linkmanRelation" + num));
                controller().setText("linkman-qq" + num, stringValue(data, "linkmanQq" + num));
            }
        }
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
