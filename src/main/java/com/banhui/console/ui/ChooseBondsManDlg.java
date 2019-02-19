package com.banhui.console.ui;

import com.banhui.console.rpc.AuthenticationProxy;
import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.DialogUtils.inputPassword;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class ChooseBondsManDlg extends DialogPane {

    private volatile long pId;
    private volatile Long auId;
    private volatile String realName;

    public ChooseBondsManDlg(
            long pId
    ) {
        if (pId != 0) {
            this.pId = pId;
            setTitle(getTitle() + pId);
        }
        controller().readOnly("bondsman_au", true);
        controller().readOnly("bondsman_select", true);
        controller().connect("chooseBondsMan", this::chooseBondsMan);
        new ProjectProxy().queryFinancierById(this.pId)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchBondMan, UPDATE_UI);
    }

    private void searchBondMan(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.auId = longValue(maps.get(2), "auId");
            if (auId != null) {
                this.realName = stringValue(maps.get(2), "realName");
                controller().setText("bondsman_au", userType(stringValue(maps.get(2), "userType")) + this.realName);
            } else {
                controller().setText("bondsman_au", "（无）");
            }

        }
    }

    private void chooseBondsMan(
            ActionEvent actionEvent
    ) {
        final ChoosePrjAccountDlg dlg = new ChoosePrjAccountDlg(3, this.realName);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            if (dlg.getAuId() != 0) {
                controller().setText("bondsman_select", userType(dlg.getUserType()) + dlg.getRealName());
                this.auId = dlg.getAuId();
            }
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            checkPwd();
        } else {
            super.done(result);
        }

    }

    public void checkPwd() {
        String inputPwd = inputPassword(getOwner(), controller().getMessage("pwd-text"), "");
        if (inputPwd == null) {
            return;
        }
        final Map<String, Object> param = new HashMap<>();
        param.put("password", inputPwd);
        new AuthenticationProxy().validateUser(param)
                                 .thenApplyAsync(Result::booleanValue)
                                 .whenCompleteAsync(this::validateUser, UPDATE_UI);

    }

    private void validateUser(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (flag) {
                doModify();
            } else {
                fail(getOwner(), controller().getMessage("pwd-fail"));
            }
        }
    }

    private void doModify() {
        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", this.pId);
        params.put("new-bondsman-au-id", auId);
        String realName1 = controller().getText("bondsman_au");
        String realName2 = controller().getText("bondsman_select");
        if (confirm(null, controller().formatMessage("confirm-text", pId, realName1, realName2))) {
            controller().disable("ok");
            new ProjectProxy().modifyBondsman(params)
                              .thenApplyAsync(Result::booleanValue)
                              .whenCompleteAsync(this::saveCallback, UPDATE_UI);
        }
    }


    private void saveCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
            controller().enable("ok");
        } else {
            super.done(OK);
        }
    }

    private String userType(String a) {
        switch (a) {
            case "1":
                return "（个人）";
            case "2":
                return "（机构）";
            default:
                return "";
        }
    }


}
