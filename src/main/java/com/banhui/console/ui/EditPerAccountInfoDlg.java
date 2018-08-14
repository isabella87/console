package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditPerAccountInfoDlg
        extends DialogPane {
    private volatile long id;
    private Map<String, Object> roleParams;

    public EditPerAccountInfoDlg(
            long id,
            long status
    ) {
        this.id = id;
        roleParams = new HashMap<>();
        if (this.id != 0) {
            setTitle(getTitle() + id);
        }
        controller().readOnly(null, true);
        updateData();
        updateButton();
        if (status == 1) {
            controller().readOnly("bank-acc-info", true);
            controller().readOnly("frozen-fund", true);
            controller().readOnly("ts-tender-deposit", true);
            controller().readOnly("ts-tender-platform", true);
            controller().readOnly("borrow-record", true);
        }
        controller().connect("bank-acc-info", this::BroseBankPerInfo);
        controller().connect("edit-acc-role", this::editAccRole);
        controller().connect("edit-acc-lvl", this::editAccLvl);
        controller().connect("recharge-list", this::BrowseRechargeList);
        controller().connect("withdraw-list", this::BrowseWithdrawList);
        controller().connect("tender-list", this::BrowseTenderList);
        controller().connect("credit-assign-list", this::BrowseCreditAssignList);
        controller().connect("payment-detail-deposit", this::BrowsePaymentDeposit);
        controller().connect("payment-detail-platform", this::BrowsePaymentPlatform);
        controller().connect("history-invests", this::BrowseHistoryInvests);
        controller().connect("bank-funds", this::BrowseBankFunds);
        controller().connect("lock-account", this::LockAccount);
        controller().connect("unlock-account", this::UnlockAccount);
        controller().connect("browse-protocol", this::BrowseProtocol);
        controller().connect("month-reports", this::BrowseMonthReports);
        controller().connect("frozen-fund", this::BrowseFrozenFund);
        controller().connect("ts-tender-deposit", this::BrowseTsTenderDeposit);
        controller().connect("ts-tender-platform", this::BrowseTsTenderPlatform);
        controller().connect("borrow-record", this::BrowseBorrowRecord);
        controller().connect("check-registry", this::CheckRegistry);
    }

    private void updateButton() {
        controller().readOnly("bank-acc-info", false);
        controller().readOnly("edit-acc-role", false);
        controller().readOnly("edit-acc-lvl", false);
        controller().readOnly("recharge-list", false);
        controller().readOnly("withdraw-list", false);
        controller().readOnly("tender-list", false);
        controller().readOnly("credit-assign-list", false);
        controller().readOnly("payment-detail-deposit", false);
        controller().readOnly("payment-detail-platform", false);
        controller().readOnly("history-invests", false);
        controller().readOnly("bank-funds", false);
        controller().readOnly("lock-account", false);
        controller().readOnly("browse-protocol", false);
        controller().readOnly("unlock-account", false);
        controller().readOnly("month-reports", false);
        controller().readOnly("frozen-fund", false);
        controller().readOnly("ts-tender-deposit", false);
        controller().readOnly("ts-tender-platform", false);
        controller().readOnly("borrow-record", false);
        controller().readOnly("check-registry", false);
    }

    private void BroseBankPerInfo(
            ActionEvent actionEvent
    ) {
        BrowseBankPerInfoDlg dlg = new BrowseBankPerInfoDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
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

    private void BrowseRechargeList(
            ActionEvent actionEvent
    ) {
        BrowseRechargeListDlg dlg = new BrowseRechargeListDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
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

    private void BrowseBankFunds(
            ActionEvent actionEvent
    ) {
        BrowseBankFundsDlg dlg = new BrowseBankFundsDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
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
                           .thenAcceptAsync(this::unLockCallBack, UPDATE_UI);
    }

    private void unLockCallBack(
            Boolean unlock
    ) {
        if (unlock) {
            controller().show("lock-account");
            controller().hide("unlock-account");
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
                           .thenAcceptAsync(this::lockCallBack, UPDATE_UI);
    }

    private void lockCallBack(
            Boolean lock
    ) {
        if (lock) {
            controller().hide("lock-account");
            controller().show("unlock-account");
        }
    }

    private void BrowseProtocol(
            ActionEvent actionEvent
    ) {
        ChooseProtocolDlg dlg = new ChooseProtocolDlg(id, 47);
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

    private void updateData() {
        assertUIThread();
        allDone(new AccountsProxy().queryAccPersonInfoById(id),
                new AccountsProxy().getAllowRoles(id)
        ).thenApply(results -> new Object[]{
                results[0].map(),
                results[1].map()
        }).whenCompleteAsync(this::updateDataCallback, UPDATE_UI);
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

    private void BrowseBorrowRecord(
            ActionEvent actionEvent
    ) {
        BrowseBorrowRecordDlg dlg = new BrowseBorrowRecordDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void CheckRegistry(
            ActionEvent actionEvent
    ) {
        final String idCard = controller().getText("id-card");
        CheckRegistryDlg dlg = new CheckRegistryDlg(id, idCard);
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

    private void updateDataInfo(
            Map<String, Object> data
    ) {
        controller().setText("login-name", stringValue(data, "loginName"));
        controller().setText("mobile", stringValue(data, "mobile"));
        controller().setInteger("lvl", intValue(data, "lvl"));
        controller().setText("email", stringValue(data, "email"));
        controller().setDate("reg-time", dateValue(data, "regTime"));
        controller().setText("recommend-mobile", stringValue(data, "recommendMobile"));
        controller().setText("real-name", stringValue(data, "realName"));
        controller().setText("position", stringValue(data, "position"));
        controller().setText("company", stringValue(data, "company"));
        controller().setText("company-type", stringValue(data, "company-type"));
        controller().setText("address", stringValue(data, "address"));
        controller().setText("postal-code", stringValue(data, "postalCode"));
        controller().setText("id-card", stringValue(data, "idCard"));
        controller().setText("home-phone", stringValue(data, "homePhone"));
        controller().setText("qq-number", stringValue(data, "qqNumber"));
        controller().setText("org-code", stringValue(data, "orgCode"));

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
}
