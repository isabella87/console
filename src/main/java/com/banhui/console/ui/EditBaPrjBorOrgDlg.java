package com.banhui.console.ui;


import com.banhui.console.rpc.BaPrjBorOrgsProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditBaPrjBorOrgDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditBaPrjBorOrgDlg.class);

    private volatile long id;

    private Map<String, Object> row;

    public EditBaPrjBorOrgDlg(
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
                params.put("bpmo-id", id);
            }

            params.put("org-name", controller().getText("org-name").trim());
            params.put("show-org-name", controller().getText("show-org-name").trim());
            params.put("registered-fund", controller().getText("registered-fund").trim());
            params.put("registered-show-fund", controller().getText("registered-show-fund").trim());
            params.put("registered-date", controller().getDate("registered-date"));
            params.put("legal-id-card", controller().getText("legal-id-card").trim());
            params.put("legal-person-name", controller().getText("legal-person-name").trim());
            params.put("legal-person-show-name", controller().getText("legal-person-show-name").trim());
            params.put("mobile", controller().getText("mobile").trim());
            params.put("email", controller().getText("email").trim());
            params.put("address", controller().getText("address").trim());
            params.put("intro", controller().getText("intro").trim());
            params.put("show-address", controller().getText("show-address").trim());
            params.put("fax", controller().getText("fax").trim());
            params.put("wchat", controller().getText("wchat").trim());
            params.put("linkman-name", controller().getText("linkman-name").trim());
            params.put("linkman-id-card", controller().getText("linkman-id-card").trim());
            params.put("linkman-mobile", controller().getText("linkman-mobile").trim());
            params.put("linkman-fax", controller().getText("linkman-fax").trim());
            params.put("linkman-email", controller().getText("linkman-email").trim());
            params.put("linkman-wchat", controller().getText("linkman-wchat").trim());
            params.put("linkman-address", controller().getText("linkman-address").trim());
            params.put("linkman-show-address", controller().getText("linkman-show-address").trim());
            params.put("linkman-show-name", controller().getText("linkman-show-name").trim());
            params.put("social-credit-code", controller().getText("social-credit-code").trim());
            params.put("show-social-credit-code", controller().getText("show-social-credit-code").trim());
            params.put("industry", controller().getText("industry").trim());
            params.put("work-address", controller().getText("work-address").trim());
            params.put("show-work-address", controller().getText("show-work-address").trim());
            params.put("shareholder-info", controller().getText("shareholder-info").trim());
            params.put("show-shareholder-info", controller().getText("show-shareholder-info").trim());
            params.put("operate-area", controller().getText("operate-area").trim());
            params.put("other-info", controller().getText("other-info").trim());

            (this.id == 0 ? new BaPrjBorOrgsProxy().add(params) : new BaPrjBorOrgsProxy().update(this.id, params))
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

        new BaPrjBorOrgsProxy().query(this.id)
                               .thenApplyAsync(Result::map)
                               .thenAcceptAsync(this::updateDataCallback, UPDATE_UI)
                               .exceptionally(ErrorHandler::handle);
    }

    @SuppressWarnings("unchecked")
    private void updateDataCallback(
            Map data
    ) {
        controller().setText("org-name", stringValue(data, "orgName"));
        controller().setText("show-org-name", stringValue(data, "showOrgName"));
        controller().setDecimal("registered-fund", decimalValue(data, "registeredFund"));
        controller().setText("registered-show-fund", stringValue(data, "registeredShowFund"));
        controller().setDate("registered-date", dateValue(data, "registeredDate"));
        controller().setText("legal-id-card", stringValue(data, "legalIdCard"));
        controller().setText("legal-person-name", stringValue(data, "legalPersonName"));
        controller().setText("legal-person-show-name", stringValue(data, "legalPersonShowName"));
        controller().setText("mobile", stringValue(data, "mobile"));
        controller().setText("email", stringValue(data, "email"));
        controller().setText("address", stringValue(data, "address"));
        controller().setText("intro", stringValue(data, "intro"));
        controller().setText("show-address", stringValue(data, "showAddress"));
        controller().setText("fax", stringValue(data, "fax"));
        controller().setText("wchat", stringValue(data, "wchat"));
        controller().setText("linkman-name", stringValue(data, "linkmanName"));
        controller().setText("linkman-id-card", stringValue(data, "linkmanIdCard"));
        controller().setText("linkman-mobile", stringValue(data, "linkmanMobile"));
        controller().setText("linkman-fax", stringValue(data, "linkmanFax"));
        controller().setText("linkman-email", stringValue(data, "linkmanEmail"));
        controller().setText("linkman-wchat", stringValue(data, "linkmanWchat"));
        controller().setText("linkman-address", stringValue(data, "linkmanAddress"));
        controller().setText("linkman-show-address", stringValue(data, "linkmanShowAddress"));
        controller().setText("linkman-show-name", stringValue(data, "linkmanShowName"));
        controller().setText("social-credit-code", stringValue(data, "socialCreditCode"));
        controller().setText("show-social-credit-code", stringValue(data, "showSocialCreditCode"));
        controller().setText("industry", stringValue(data, "industry"));
        controller().setText("work-address", stringValue(data, "workAddress"));
        controller().setText("show-work-address", stringValue(data, "showWorkAddress"));
        controller().setText("shareholder-info", stringValue(data, "shareholderInfo"));
        controller().setText("show-shareholder-info", stringValue(data, "showShareholderInfo"));
        controller().setText("operate-area", stringValue(data, "operateArea"));
        controller().setText("other-info", stringValue(data, "otherInfo"));

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
