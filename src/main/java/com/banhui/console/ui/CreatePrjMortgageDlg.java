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

public class CreatePrjMortgageDlg
        extends DialogPane {

    private volatile long id;
    private volatile long bpmId;
    private Map<String, Object> row;
    private List<Long> ids;

    public CreatePrjMortgageDlg(
            long id,
            List<Long> ids
    ) {
        this.id = id;
        this.ids = ids;
        controller().readOnly("name", true);
        controller().connect("select", this::select);
    }

    private void select(
            ActionEvent actionEvent
    ) {
        final ChoosePrjMortgageDlg dlg = new ChoosePrjMortgageDlg();
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            if (dlg.getBpmId() != 0) {
                this.bpmId = dlg.getBpmId();
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
                        String mortgageBlank = controller().formatMessage("mortgage-blank");
                        fail(getOwner(), mortgageBlank);
            } else if(this.ids.contains(this.bpmId)){
                String repeatText = controller().formatMessage("repeat-text");
                fail(getOwner(), repeatText);
            }else {
                controller().disable("ok");
                final Map<String, Object> params = new HashMap<>();

                if (this.id != 0) {
                    params.put("p-id", id);
                }
                params.put("intro", controller().getText("intro"));
                params.put("visible", controller().getNumber("visible"));
                params.put("guara-high-credit-amt", controller().getDecimal("guara-high-credit-amt"));
                params.put("order-no", controller().getNumber("order-no"));
                        params.put("bpm-Id", this.bpmId);
                        new ProjectProxy().createMortgage(params)
                                          .thenApplyAsync(Result::map)
                                          .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                          .exceptionally(ErrorHandler::handle)
                                          .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);
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
