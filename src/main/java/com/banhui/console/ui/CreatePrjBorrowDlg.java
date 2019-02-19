package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CreatePrjBorrowDlg
        extends DialogPane {

    private volatile long id;
    private volatile int role;
    private volatile long bpmpId;
    private volatile long bpmoId;
    private Map<String, Object> row;
    private List<Long> ids;

    public CreatePrjBorrowDlg(
            long id,
            int role,
            List<Long> ids
    ) {
        this.id = id;
        this.role = role;
        this.ids = ids;
        if (this.id != 0) {
            if (role == 3) {
                setTitle(controller().getMessage("person") + id);
                controller().setText("thisName", "借款人：");
            } else if (role == 4) {
                setTitle(controller().getMessage("org") + id);
                controller().setText("thisName", "借款机构：");
            }
        }
        controller().readOnly("name", true);
        controller().connect("select", this::select);
    }

    private void select(
            ActionEvent actionEvent
    ) {
        final ChoosePrjGuaranteeBorDlg dlg = new ChoosePrjGuaranteeBorDlg(role);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            if (dlg.getBpmpId() != 0 || dlg.getBpmoId() != 0) {
                this.bpmpId = dlg.getBpmpId();
                this.bpmoId = dlg.getBpmoId();
                controller().setText("name", dlg.getName());
            }
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            if (controller().getText("name") == null) {
                switch (role) {
                    case 3:
                        String personBlank = controller().formatMessage("person-blank");
                        fail(getOwner(), personBlank);
                        break;
                    case 4:
                        String orgBlank = controller().formatMessage("org-blank");
                        fail(getOwner(), orgBlank);
                        break;
                }
            } else if (role == 3 && ids != null && ids.contains(this.bpmpId)) {
                String repeatText = controller().formatMessage("repeatPer-text");
                fail(getOwner(), repeatText);
            } else if (role == 4 && ids != null && ids.contains(this.bpmoId)) {
                String repeatText = controller().formatMessage("repeatOrg-text");
                fail(getOwner(), repeatText);
            } else {
                controller().disable("ok");
                final Map<String, Object> params = new HashMap<>();

                if (this.id != 0) {
                    params.put("p-id", id);
                }
                params.put("loan-bal", controller().getDecimal("loan-bal"));
                params.put("other-loan-bal", controller().getDecimal("other-loan-bal"));
                params.put("overdue-num", controller().getNumber("overdue-num"));
                params.put("overdue-amt", controller().getDecimal("overdue-amt"));
                params.put("other-overdue-num", controller().getNumber("other-overdue-num"));
                params.put("visible", controller().getNumber("visible"));
                params.put("other-overdue-amt", controller().getDecimal("other-overdue-amt"));
                params.put("order-no", controller().getNumber("order-no"));
                switch (role) {
                    case 3:
                        params.put("bpmp-id", this.bpmpId);
                        new ProjectProxy().createBorPerson(params)
                                          .thenApplyAsync(Result::map)
                                          .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                          .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
                        break;
                    case 4:
                        params.put("bpmo-id", this.bpmoId);
                        new ProjectProxy().createBorOrg(params)
                                          .thenApplyAsync(Result::map)
                                          .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                          .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
                }
            }
        } else {
            super.done(result);
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
