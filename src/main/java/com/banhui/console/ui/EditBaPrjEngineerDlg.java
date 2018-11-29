package com.banhui.console.ui;


import com.banhui.console.rpc.BaPrjEngineersProxy;
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

public class EditBaPrjEngineerDlg
        extends DialogPane {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(EditBaPrjEngineerDlg.class);

    private volatile long id;
    private Map<String, Object> row;

    public EditBaPrjEngineerDlg(
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
                params.put("bpe-id", id);
            }
            params.put("name", controller().getText("name").trim());
            params.put("address", controller().getText("address").trim());
            params.put("prj-start-time", controller().getDate("prj-start-time"));
            params.put("prj-end-time", controller().getDate("prj-end-time"));
            params.put("eng-type", controller().getText("eng-type").trim());
            params.put("area", controller().getText("area").trim());
            params.put("all-org", controller().getText("all-org").trim());
            params.put("design-org", controller().getText("design-org").trim());
            params.put("mgr-org", controller().getText("mgr-org").trim());
            params.put("pro-intro", controller().getText("pro-intro").trim());
            params.put("eng-show-name", controller().getText("eng-show-name").trim());
            params.put("eng-show-address", controller().getText("eng-show-address").trim());
            params.put("show-mgr-org", controller().getText("show-mgr-org").trim());
            params.put("show-design-org", controller().getText("show-design-org").trim());
            params.put("show-area", controller().getText("show-area").trim());
            params.put("design-org-level", controller().getText("design-org-level").trim());
            params.put("mgr-org-level", controller().getText("mgr-org-level").trim());
            params.put("show-all-org", controller().getText("show-all-org").trim());
            params.put("mgr-real-name", controller().getText("mgr-real-name").trim());
            params.put("mgr-show-name", controller().getText("mgr-show-name").trim());
            params.put("qualification", controller().getText("qualification").trim());
            params.put("mgr-intro", controller().getText("mgr-intro").trim());
            params.put("mgr-gender", controller().getText("mgr-gender").trim());
            params.put("mgr-age", controller().getText("mgr-age").trim());
            params.put("mgr-show-age", controller().getText("mgr-show-age").trim());

            (this.id == 0 ? new BaPrjEngineersProxy().add(params) : new BaPrjEngineersProxy().update(this.id, params))
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

        new BaPrjEngineersProxy().query(this.id)
                                 .thenApplyAsync(Result::map)
                                 .thenAcceptAsync(this::updateDataCallback, UPDATE_UI)
                                 .exceptionally(ErrorHandler::handle);
    }

    private void updateDataCallback(
            Map<String, Object> data
    ) {
        controller().setText("name", stringValue(data, "name"));
        controller().setText("address", stringValue(data, "address"));
        controller().setDate("prj-start-time", dateValue(data, "prjStartTime"));
        controller().setDate("prj-end-time", dateValue(data, "prjEndTime"));
        controller().setText("eng-type", stringValue(data, "engType"));
        controller().setDecimal("area", decimalValue(data, "area"));
        controller().setText("all-org", stringValue(data, "allOrg"));
        controller().setText("design-org", stringValue(data, "designOrg"));
        controller().setText("mgr-org", stringValue(data, "mgrOrg"));
        controller().setText("pro-intro", stringValue(data, "proIntro"));
        controller().setText("eng-show-name", stringValue(data, "engShowName"));
        controller().setText("eng-show-address", stringValue(data, "engShowAddress"));
        controller().setText("show-mgr-org", stringValue(data, "showMgrOrg"));
        controller().setText("show-design-org", stringValue(data, "showDesignOrg"));
        controller().setText("show-area", stringValue(data, "showArea"));
        controller().setText("design-org-level", stringValue(data, "designOrgLevel"));
        controller().setText("mgr-org-level", stringValue(data, "mgrOrgLevel"));
        controller().setText("mgr-org-level", stringValue(data, "mgrOrgLevel"));
        controller().setText("show-all-org", stringValue(data, "showAllOrg"));
        controller().setText("mgr-real-name", stringValue(data, "mgrRealName"));
        controller().setText("mgr-show-name", stringValue(data, "mgrShowName"));
        controller().setText("qualification", stringValue(data, "qualification"));
        controller().setText("mgr-intro", stringValue(data, "mgrIntro"));
        controller().setText("mgr-gender", stringValue(data, "mgrGender"));
        controller().setNumber("mgr-age", longValue(data, "mgrAge"));
        controller().setText("mgr-show-age", stringValue(data, "mgrShowAge"));


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
