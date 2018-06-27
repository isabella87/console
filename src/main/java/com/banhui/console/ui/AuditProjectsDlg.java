package com.banhui.console.ui;


import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.DialogUtils.inputText;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;


public class AuditProjectsDlg extends DialogPane {

    private volatile long id;
    private volatile int role;

    private Map<String, Object> row;

    public AuditProjectsDlg(
            long id,
            long status
    ) {
        this.id = id;
        role = (int) status;
        setTitle(getTitle() + id);

        controller().connect("npass", this::npass);//不通过
        controller().connect("auctionsPass", this::auctionsPass);//不通过且流标
        controller().connect("pass", this::pass);//通过
        controller().connect("auctions", this::auctions);//执行流标
        controller().connect("tender", this::tender);//查看投标
        controller().connect("investor", this::investor);//生成出借人信息表
        controller().connect("loan", this::loan);//查看放款记录
        controller().connect("investment", this::investment);//有效投资
        controller().connect("completed", this::completed);//结清
        controller().connect("lock", this::lock);//锁定
        controller().connect("unlock", this::unlock);//解锁

        controller().hide("auctions");
        controller().hide("tender");
        controller().hide("investor");
        controller().hide("loan");
        controller().hide("investment");
        controller().hide("comments");
        controller().hide("beizhu");
        controller().hide("auctionsPass");
        controller().hide("completed");
        controller().hide("lock");
        controller().hide("unlock");

        new AuditProxy().queryAudit(id)
                        .thenApplyAsync(Result::list)
                        .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                        .exceptionally(ErrorHandler::handle);
        getUi();
        new AuditProxy().prjLockStatus(id)
                        .thenApplyAsync(Result::longValue)
                        .thenAcceptAsync(this::lockStatus, UPDATE_UI);
    }


    private void lockStatus(
            long status
    ) {
        if (status == 1L) {
            controller().hide("pass");
            controller().hide("npass");
            controller().hide("auctions");
            controller().hide("auctionsPass");
            controller().hide("completed");
            controller().hide("lock");
            controller().hide("beizhu");
            controller().hide("comments");
            controller().show("unlock");
        } else {
            controller().show("lock");
            controller().hide("unlock");
        }
    }

    //锁定
    private void lock(
            ActionEvent actionEvent
    ) {
        new AuditProxy().lockPrj(id)
                        .thenApplyAsync(Result::map);
        controller().hide("pass");
        controller().hide("npass");
        controller().hide("auctions");
        controller().hide("auctionsPass");
        controller().hide("completed");
        controller().hide("lock");
        controller().hide("beizhu");
        controller().hide("comments");
        controller().show("unlock");
    }

    //解锁
    private void unlock(
            ActionEvent actionEvent
    ) {
        new AuditProxy().unlockPrj(id)
                        .thenApplyAsync(Result::map);
        controller().show("lock");
        controller().hide("unlock");
        getUi();
    }

    //结清
    private void completed(ActionEvent actionEvent) {

        new AuditProxy().prjBonus(id)
                        .thenApplyAsync(Result::list)
                        .thenAcceptAsync(this::updateUnPaidAmt, UPDATE_UI)
                        .exceptionally(ErrorHandler::handle);
    }

    private void updateUnPaidAmt(
            Collection<Map<String, Object>> rows
    ) {
        final BigDecimal unPaidAmt;
        if (rows == null || rows.isEmpty()) {
            unPaidAmt = BigDecimal.ZERO;
        } else {
            unPaidAmt = rows.stream()
                            .map(item -> decimalValue(item, "unpaidAmt"))
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        String confirmClose = controller().formatMessage("confirm-close", unPaidAmt);
        String confirmCompleted = controller().formatMessage("confirm-completed");

        if (confirm(this.getOwner(), confirmClose, confirmCompleted)) {
            new AuditProxy().completedPrj(id)
                            .thenApplyAsync(Result::map)
                            .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                            .exceptionally(ErrorHandler::handle);
        }
    }

    //有效投资
    private void investment(
            ActionEvent actionEvent
    ) {
        final BrowsePrjInvestmentDlg dlg = new BrowsePrjInvestmentDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    //查看放款信息
    private void loan(
            ActionEvent actionEvent
    ) {
        final BrowsePrjLoanDlg dlg = new BrowsePrjLoanDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    //生成出借人信息表
    private void investor(
            ActionEvent actionEvent
    ) {
        final BrowsePrjInvestorsDlg dlg = new BrowsePrjInvestorsDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    //查看投标
    private void tender(
            ActionEvent actionEvent
    ) {
        final BrowsePrjTenderDlg dlg = new BrowsePrjTenderDlg(id, role);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    //执行流标
    private void auctions(
            ActionEvent actionEvent
    ) {
        final BrowsePrjCancelTendersDlg dlg = new BrowsePrjCancelTendersDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }


    //不通过并且流标
    private void auctionsPass(
            ActionEvent actionEvent
    ) {
        final String comments = controller().getText("comments").trim();
        if (comments.equals("")) {
            String confirmNpassBlank = controller().formatMessage("confirm-npass-blank");
            fail(getOwner(), confirmNpassBlank);
        } else {
            String confirmAuctionsPassText = controller().formatMessage("confirm-auctionsPass-text");
            String confirmAuctionsPass = controller().formatMessage("confirm-auctionsPass");
            if (confirm(this.getOwner(), confirmAuctionsPassText, confirmAuctionsPass)) {
                final Map<String, Object> params = new HashMap<>();
                controller().disable("auctions");
                if (this.id != 0) {
                    params.put("p-id", id);
                }
                params.put("comments", controller().getText("comments").trim());
                new AuditProxy().busVpStopRaising(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("auctions"), UPDATE_UI);
            }
        }
    }

    //不通过
    public void npass(
            ActionEvent actionEvent
    ) {
        final String comments = controller().getText("comments").trim();
        if (comments.equals("")) {
            String confirmNpassBlank = controller().formatMessage("confirm-npass-blank");
            fail(getOwner(), confirmNpassBlank);
        } else {
            final Map<String, Object> params = new HashMap<>();
            controller().disable("pass");
            controller().disable("npass");
            if (this.id != 0) {
                params.put("p-id", id);
            }
            params.put("flag", false);
            params.put("comments", controller().getText("comments").trim());
            params.put("signature", null);
            doAudit(params);
            this.done(OK);
        }
    }

    //通过
    public void pass(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
//        controller().disable("pass");
//        controller().disable("npass");
        if (this.id != 0) {
            params.put("p-id", id);
        }
        params.put("flag", true);
        params.put("comments", controller().getText("comments").trim());
        params.put("signature", null);
        if (role == 60) {
            String bondText = inputText(getOwner(), "输入财务核收服务费", "0");
            long bond;
            if (bondText != null) {
                bond = Long.valueOf(bondText);
            } else {
                return;
            }
            final Map<String, Object> params2 = new HashMap<>();
            params2.put("p-id", id);
            params2.put("bond_amt", bond);
            new AuditProxy().updatePrjBond(params2)
                            .thenApplyAsync(Result::map)
                            .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                            .exceptionally(ErrorHandler::handle)
                            .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
        }
        doAudit(params);
        this.done(OK);
    }

    public void doAudit(
            Map<String, Object> params
    ) {
        if (this.id != 0) {
            params.put("p-id", id);
        }
        switch (role) {
            case 0:
                new AuditProxy().prjSubmit(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 1:
                new AuditProxy().prjMgrAudit(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 10:
                new AuditProxy().riskCtrlAudit(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 20:
                new AuditProxy().busSecAudit(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 30:
                new AuditProxy().busVpAprOnLine(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 50:
                new AuditProxy().busVpConfirmFull(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 60:
                new AuditProxy().checkFee(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);

                break;
            case 70:
                params.put("s-datepoint", null);
                new AuditProxy().busVpConfirmLoan(params)
                                .thenApplyAsync(Result::map)
                                .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                .exceptionally(ErrorHandler::handle)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
        }
    }


    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(c);

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

    public void getUi() {
        switch (role) {
            case 0:
                controller().setText("role", "提交项目经理审批");
                controller().show("comments");
                controller().show("beizhu");
                controller().hide("npass");
                break;
            case 1:
                controller().setText("role", "项目经理审批");
                controller().show("comments");
                controller().show("beizhu");
                break;
            case 10:
                controller().setText("role", "风控审批");
                controller().show("comments");
                controller().show("beizhu");
                break;
            case 20:
                controller().setText("role", "待评委会审批");
                controller().show("comments");
                controller().show("beizhu");
                break;
            case 30:
                controller().setText("role", "业务副总批准上线");
                controller().show("comments");
                controller().show("beizhu");
                break;
            case 40:
                controller().show("tender");
                controller().show("auctions");
                controller().hide("npass");
                controller().hide("pass");
                break;
            case 50:
                controller().setText("role", "业务副总确认满标");
                controller().show("tender");
                controller().show("comments");
                controller().show("beizhu");
                controller().show("auctions");
                controller().hide("npass");
                controller().show("auctionsPass");
                break;
            case 60:
                controller().setText("role", "财务核收服务费");
                controller().show("tender");
                controller().show("comments");
                controller().show("beizhu");
                controller().show("auctions");
                controller().hide("npass");
                break;
            case 70:
                controller().setText("role", "业务副总批准放款");
                controller().show("tender");
                controller().show("comments");
                controller().show("beizhu");
                controller().show("auctions");
                controller().hide("npass");
                controller().show("auctionsPass");
                break;
            case 80:
                controller().show("investor");
                controller().show("tender");
                controller().show("loan");
                controller().show("auctions");
                controller().hide("npass");
                controller().hide("pass");
                controller().hide("comments");
                break;
            case 90:
                controller().show("investor");
                controller().show("tender");
                controller().show("loan");
                controller().show("auctions");
                controller().show("investment");
                controller().show("completed");
                controller().hide("npass");
                controller().hide("pass");
                controller().hide("comments");
                break;
            case 999:
                controller().show("investor");
                controller().show("tender");
                controller().show("loan");
                controller().show("auctions");
                controller().show("investment");
                controller().hide("npass");
                controller().hide("pass");
                controller().hide("comments");
                break;
            case -1:
                controller().show("auctions");
                controller().show("tender");
                controller().hide("npass");
                controller().hide("pass");
                controller().hide("comments");
                break;
        }

    }
}
