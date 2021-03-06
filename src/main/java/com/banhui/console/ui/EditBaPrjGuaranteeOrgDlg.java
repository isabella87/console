package com.banhui.console.ui;


import com.banhui.console.rpc.BaPrjGuaranteeOrgsProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditBaPrjGuaranteeOrgDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditBaPrjGuaranteeOrgDlg.class);

    private volatile long id;
    private Map<String, Object> row;

    public EditBaPrjGuaranteeOrgDlg(
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
                params.put("bgo-id", id);
            }

            params.put("id", this.id);
            params.put("name", controller().getText("name").trim());
            params.put("show-name", controller().getText("show-name").trim());
            params.put("registered-date", controller().getDate("registered-date"));
            params.put("reg-funds", controller().getNumber("reg-funds"));
            params.put("reg-address", controller().getText("reg-address").trim());
            params.put("show-reg-address", controller().getText("show-reg-address").trim());
            params.put("postcode", controller().getText("postcode").trim());
            params.put("legal-id-card", controller().getText("legal-id-card").trim());
            params.put("legal-person-name", controller().getText("legal-person-name").trim());
            params.put("legal-person-show-name", controller().getText("legal-person-show-name").trim());
            params.put("linkman", controller().getText("linkman").trim());
            params.put("mobile", controller().getText("mobile").trim());
            params.put("qualification", controller().getText("qualification").trim());
            params.put("social-credit-code", controller().getText("social-credit-code").trim());
            params.put("show-social-credit-code", controller().getText("show-social-credit-code").trim());
            params.put("intro", controller().getText("intro").trim());

            (this.id == 0 ? new BaPrjGuaranteeOrgsProxy().add(params) : new BaPrjGuaranteeOrgsProxy().update(params))
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
        new BaPrjGuaranteeOrgsProxy().query(this.id)
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
            controller().setText("name", stringValue(data, "name"));
            controller().setText("show-name", stringValue(data, "showName"));
            controller().setDate("registered-date", dateValue(data, "registeredDate"));
            controller().setNumber("reg-funds", longValue(data, "regFunds"));
            controller().setText("reg-address", stringValue(data, "regAddress"));
            controller().setText("show-reg-address", stringValue(data, "showRegAddress"));
            controller().setText("postcode", stringValue(data, "postcode"));
            controller().setText("legal-id-card", stringValue(data, "legalIdCard"));
            controller().setText("legal-person-name", stringValue(data, "legalPersonName"));
            controller().setText("legal-person-show-name", stringValue(data, "legalPersonShowName"));
            controller().setText("linkman", stringValue(data, "linkman"));
            controller().setText("mobile", stringValue(data, "mobile"));
            controller().setText("qualification", stringValue(data, "qualification"));
            controller().setText("social-credit-code", stringValue(data, "socialCreditCode"));
            controller().setText("show-social-credit-code", stringValue(data, "showSocialCreditCode"));
            controller().setText("intro", stringValue(data, "intro"));
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
