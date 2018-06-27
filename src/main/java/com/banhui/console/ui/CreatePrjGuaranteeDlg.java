package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CreatePrjGuaranteeDlg
        extends DialogPane {
    private volatile int role;
    private volatile long bgpId;
    private volatile long bgoId;
    private volatile long id;
    private Map<String, Object> row;

    public CreatePrjGuaranteeDlg(
            long id,
            int role
    ) {
        this.id = id;
        this.role = role;
        if (this.id != 0) {
            if (role == 1) {
                setTitle(controller().getMessage("person") + id);
                controller().setText("thisName", "担保人：");
            } else if (role == 2) {
                setTitle(controller().getMessage("org") + id);
                controller().setText("thisName", "担保机构：");
            }
        }

        controller().readOnly("show-name", true);

        controller().setText("form", null);
        controller().setText("range", null);
        controller().setText("limit", null);
        controller().setText("relation-ship", null);
        controller().setText("name", "（无）");

        controller().connect("select", this::select);
    }

    private void select(
            ActionEvent actionEvent
    ) {
        final ChoosePrjGuaranteeBorDlg dlg = new ChoosePrjGuaranteeBorDlg(role);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            if (dlg.getBgpId() != 0 || dlg.getBgoId() != 0) {
                this.bgpId = dlg.getBgpId();
                this.bgoId = dlg.getBgoId();
                controller().setText("name", dlg.getName());
                controller().setText("show-name", dlg.getShowName());
            }
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            if (controller().getText("show-name") == null) {
                switch (role) {
                    case 1:
                        String personBlank = controller().formatMessage("person-blank");
                        fail(getOwner(), personBlank);
                        break;
                    case 2:
                        String orgBlank = controller().formatMessage("org-blank");
                        fail(getOwner(), orgBlank);
                        break;
                }
            } else {
                controller().disable("ok");
                final Map<String, Object> params = new HashMap<>();

                if (this.id != 0) {
                    params.put("p-id", id);
                }
                params.put("form", controller().getText("form").trim());
                params.put("limit", controller().getText("limit").trim());
                params.put("range", controller().getText("range").trim());
                params.put("relation-ship", controller().getText("relation-ship").trim());
                params.put("last-year-income", controller().getDecimal("last-year-income"));
                params.put("guarantee-right-man", controller().getText("guarantee-right-man").trim());
                params.put("guarantee-right-man-no", controller().getText("guarantee-right-man-no").trim());
                params.put("order-no", controller().getNumber("order-no"));
                params.put("visible", controller().getNumber("visible"));
                params.put("guara-high-credit-amt", controller().getDecimal("guara-high-credit-amt"));
                params.put("main-credit-amt", controller().getDecimal("main-credit-amt"));
                params.put("intro", controller().getText("intro").trim());
                switch (role) {
                    case 1:
                        params.put("bgp-id", this.bgpId);
                        new ProjectProxy().createPrjGuaranteePerson(params)
                                          .thenApplyAsync(Result::map)
                                          .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                          .exceptionally(ErrorHandler::handle)
                                          .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
                        break;
                    case 2:
                        params.put("bgo-id", this.bgoId);
                        new ProjectProxy().createPrjGuaranteeOrg(params)
                                          .thenApplyAsync(Result::map)
                                          .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                          .exceptionally(ErrorHandler::handle)
                                          .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
                }

            }
        } else {
            super.done(result);
        }
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
