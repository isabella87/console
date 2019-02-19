package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static com.banhui.console.rpc.ResultUtils.dateValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.DialogUtils.prompt;

public class EditOrgAccountInfoDlg
        extends DialogPane {

    private volatile long id;
    private Map<String, Object> roleParams;

    public EditOrgAccountInfoDlg(
            long id
    ) {
        this.id = id;
        if (this.id != 0) {
            setTitle(getTitle() + id);
        }
        roleParams = new HashMap<>();
        updateData();

        controller().readOnly("login-name", true);
        controller().readOnly("mobile", true);
        controller().readOnly("create-time", true);
        controller().readOnly("update-time", true);
        controller().readOnly("update-time", true);
        controller().readOnly("lvl", true);

        controller().connect("commit-info", this::commitInfo);
        controller().connect("edit-acc-role", this::editAccRole);
        controller().connect("edit-acc-lvl", this::editAccLvl);
        controller().connect("acc-auto-tender", this::accAutoTender);
        controller().connect("withdraw-list", this::BrowseWithdrawList);
        controller().connect("tender-list", this::BrowseTenderList);
        controller().connect("credit-assign-list", this::BrowseCreditAssignList);
        controller().connect("bank-funds", this::BrowseBankFunds);
        controller().connect("lock-account", this::LockAccount);
        controller().connect("unlock-account", this::UnlockAccount);
        controller().connect("payment-detail-platform", this::BrowsePaymentPlatform);
        controller().connect("payment-detail-deposit", this::BrowsePaymentDeposit);
        controller().connect("history-invests", this::BrowseHistoryInvests);
        controller().connect("borrow-record", this::BrowseBorrowRecord);
        controller().connect("month-reports", this::BrowseMonthReports);
        controller().connect("ts-tender-deposit", this::BrowseTsTenderDeposit);
        controller().connect("ts-tender-platform", this::BrowseTsTenderPlatform);
        controller().connect("frozen-fund", this::BrowseFrozenFund);
        controller().connect("browse-protocol", this::BrowseProtocol);
        controller().connect("confirm-registry", this::ConfirmRegistry);
        controller().connect("bank-acc-info", this::BrowseBankOrgInfoDlg);
    }


    private void updateData() {
        assertUIThread();
        allDone(new AccountsProxy().queryAccOrgInfoById(id),
                new AccountsProxy().getAllowRoles(id)
        ).thenApply(results -> new Object[]{
                results[0].map(),
                results[1].map()
        }).whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
    }

    private void accAutoTender(
            ActionEvent actionEvent
    ) {
        BrowseAccAutoTenderDlg dlg = new BrowseAccAutoTenderDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    @SuppressWarnings("unchecked")
    private void updateDataCallback(
            Object[] results,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            updateDataInfo((Map<String, Object>) results[0]);
            updateAllowRole((Map<String, Object>) results[1]);
        }
    }

    private void UnlockAccount(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        if (this.id != 0) {
            params.put("au-id", id);
        }
        params.put("enable", false);
        new AccountsProxy().lock(params)
                           .thenApplyAsync(Result::booleanValue)
                           .whenCompleteAsync(this::unLockCallBack, UPDATE_UI);
    }

    private void unLockCallBack(
            Boolean unlock,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (unlock) {
                controller().show("lock-account");
                controller().hide("unlock-account");
            }
        }
    }

    private void LockAccount(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        if (this.id != 0) {
            params.put("au-id", id);
        }
        params.put("enable", true);
        new AccountsProxy().lock(params)
                           .thenApplyAsync(Result::booleanValue)
                           .whenCompleteAsync(this::lockCallBack, UPDATE_UI);
    }

    private void lockCallBack(
            Boolean lock,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (lock) {
                controller().hide("lock-account");
                controller().show("unlock-account");
            }
        }
    }

    private void editAccRole(
            ActionEvent actionEvent
    ) {
        roleParams.put("auId", id);
        EditAccRoleDlg dlg = new EditAccRoleDlg(roleParams);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            updateAllowRole(dlg.getRollParams());
        }
    }

    private void editAccLvl(
            ActionEvent actionEvent
    ) {
        final int lvl = controller().getInteger("lvl");
        EditAccLvlDlg dlg = new EditAccLvlDlg(id, lvl);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            controller().setInteger("lvl", dlg.getLev());
        }
    }

    private void BrowseWithdrawList(
            ActionEvent actionEvent
    ) {
        BrowseWithdrawListDlg dlg = new BrowseWithdrawListDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseTenderList(
            ActionEvent actionEvent
    ) {
        BrowseTenderListDlg dlg = new BrowseTenderListDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseCreditAssignList(
            ActionEvent actionEvent
    ) {
        BrowseCreditAssignListDlg dlg = new BrowseCreditAssignListDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseBankFunds(
            ActionEvent actionEvent
    ) {
        BrowseBankFundsDlg dlg = new BrowseBankFundsDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowsePaymentDeposit(
            ActionEvent actionEvent
    ) {
        BrowsePaymentDepositDlg dlg = new BrowsePaymentDepositDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowsePaymentPlatform(
            ActionEvent actionEvent
    ) {
        BrowsePaymentPlatformDlg dlg = new BrowsePaymentPlatformDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseHistoryInvests(
            ActionEvent actionEvent
    ) {
        BrowseHistoryInvestsDlg dlg = new BrowseHistoryInvestsDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseBorrowRecord(
            ActionEvent actionEvent
    ) {
        BrowseBorrowRecordDlg dlg = new BrowseBorrowRecordDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseMonthReports(
            ActionEvent actionEvent
    ) {
        BrowseMonthReportsDlg dlg = new BrowseMonthReportsDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseTsTenderDeposit(
            ActionEvent actionEvent
    ) {
        BrowseTsTenderDepositDlg dlg = new BrowseTsTenderDepositDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseTsTenderPlatform(
            ActionEvent actionEvent
    ) {
        BrowseTsTenderPlatformDlg dlg = new BrowseTsTenderPlatformDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseFrozenFund(
            ActionEvent actionEvent
    ) {
        BrowseFrozenFundDlg dlg = new BrowseFrozenFundDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseProtocol(
            ActionEvent actionEvent
    ) {
        ChooseProtocolDlg dlg = new ChooseProtocolDlg(id, 47, 2);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void ConfirmRegistry(
            ActionEvent actionEvent
    ) {
        ConfirmRegistryDlg dlg = new ConfirmRegistryDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void BrowseBankOrgInfoDlg(
            ActionEvent actionEvent
    ) {
        BrowseBankOrgInfoDlg dlg = new BrowseBankOrgInfoDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void updateDataInfo(
            Map<String, Object> data
    ) {
        controller().setText("login-name", stringValue(data, "loginName"));
        controller().setText("real-name", stringValue(data, "realName"));
        controller().setText("position", stringValue(data, "position"));
        controller().setText("id-card", stringValue(data, "idCard"));
        controller().setText("mobile", stringValue(data, "mobile"));
        controller().setText("email", stringValue(data, "email"));
        controller().setText("qq-number", stringValue(data, "qqNumber"));
        controller().setText("company", stringValue(data, "company"));
        controller().setDate("create-time", dateValue(data, "createTime"));
        controller().setDate("update-time", dateValue(data, "updateTime"));
        controller().setInteger("lvl", intValue(data, "lvl"));
        controller().setText("org-name", stringValue(data, "orgName"));
        controller().setText("company-type", stringValue(data, "companyType"));
        controller().setText("address", stringValue(data, "address"));
        controller().setText("postal-code", stringValue(data, "postalCode"));
        controller().setText("buss-lic", stringValue(data, "bussLic"));
        controller().setText("org-code-no", stringValue(data, "orgCodeNo"));
        controller().setText("law-name", stringValue(data, "lawName"));
        controller().setText("law-id-card", stringValue(data, "lawIdCard"));
        controller().setText("home-phone", stringValue(data, "homePhone"));
        controller().setText("acc-bank", stringValue(data, "accBank"));
        controller().setText("account", stringValue(data, "account"));
        controller().setText("acc-user-name", stringValue(data, "accUserName"));
        final Long locked = longValue(data, "locked");
        if (locked != null && locked == 0) {
            controller().show("lock-account");
            controller().hide("unlock-account");
        } else {
            controller().hide("lock-account");
            controller().show("unlock-account");
        }
    }

    private void updateAllowRole(
            Map<String, Object> data
    ) {
        final Boolean allowInvest = booleanValue(data, "allowInvest");
        final Boolean allowBorrow = booleanValue(data, "allowBorrow");
        final Boolean allowCommutate = booleanValue(data, "allowCommutate");

        roleParams.put("allowInvest", allowInvest);
        roleParams.put("allowBorrow", allowBorrow);
        roleParams.put("allowCommutate", allowCommutate);

        StringBuffer allowText = new StringBuffer();
        if (allowInvest != null && allowInvest) {
            allowText.append(controller().getMessage("allow-invest"));
        }
        if (allowBorrow != null && allowBorrow) {
            allowText.append(controller().getMessage("allow-borrow"));
        }
        if (allowCommutate != null && allowCommutate) {
            allowText.append(controller().getMessage("allow-commutate"));
        }
        if (allowText.length() > 0) {
            String allow = allowText.substring(0, allowText.lastIndexOf("，"));
            controller().setText("accAllow", allow);
        } else {
            controller().setText("accAllow", null);
        }
    }

    private void commitInfo(
            ActionEvent actionEvent
    ) {
        controller().disable("commit-info");
        final Map<String, Object> params = new HashMap<>();
        if (this.id != 0) {
            params.put("au-id", id);
        }
        params.put("position", controller().getText("position").trim());
        params.put("company", controller().getText("company").trim());
        params.put("company-type", controller().getText("company-type").trim());
        params.put("address", controller().getText("address").trim());
        params.put("postal-code", controller().getText("postal-code").trim());
        params.put("home-phone", controller().getText("home-phone").trim());
        params.put("qq-number", controller().getText("qq-number").trim());
        params.put("law-name", controller().getText("law-name").trim());
        params.put("law-id-card", controller().getText("law-id-card").trim());
        params.put("org-name", controller().getText("org-name").trim());
        params.put("buss-lic", controller().getText("buss-lic").trim());
//        params.put("tax-lic", controller().getText("tax-lic").trim());
        params.put("org-code-no", controller().getText("org-code-no").trim());
        params.put("acc-user-name", controller().getText("acc-user-name").trim());
        params.put("account", controller().getText("account").trim());
        params.put("account-bank", controller().getText("acc-bank").trim());
        params.put("real-name", controller().getText("real-name").trim());
        params.put("id-card", controller().getText("id-card").trim());
        params.put("email", controller().getText("email").trim());

        new AccountsProxy().updateOrgInfo(params)
                           .thenApplyAsync(Result::booleanValue)
                           .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                           .thenAcceptAsync(v -> controller().enable("commit-info"), UPDATE_UI);
    }

    private void saveCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (flag) {
                prompt(null, controller().getMessage("commit-success"));
            } else {
                prompt(null, controller().getMessage("commit-fail"));
            }
        }
    }
}
