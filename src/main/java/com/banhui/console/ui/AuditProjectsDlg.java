package com.banhui.console.ui;


import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.ProjectRepayProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

        controller().connect("npass", this::npass);
        controller().connect("auctionsPass", this::auctionsPass);
        controller().connect("pass", this::pass);
        controller().connect("auctions", this::auctions);
        controller().connect("tender", this::tender);
        controller().connect("investor", this::investor);
        controller().connect("loan", this::loan);
        controller().connect("investment", this::investment);
        controller().connect("completed", this::completed);
        controller().connect("lock", this::lock);
        controller().connect("unlock", this::unlock);
        controller().connect("projectProtocol", this::projectProtocol);
        controller().connect("billProtocol", this::billProtocol);
        controller().connect("checkProtocol", this::checkProtocol);
        controller().connect("autoTender", this::autoTender);

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
        controller().hide("projectProtocol");
        controller().hide("billProtocol");
        controller().hide("checkProtocol");
        controller().hide("autoTender");

        new AuditProxy().queryAudit(id)
                        .thenApplyAsync(Result::list)
                        .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                        .exceptionally(ErrorHandler::handle);
        getUi();
        new AuditProxy().prjLockStatus(id)
                        .thenApplyAsync(Result::longValue)
                        .thenAcceptAsync(this::lockStatus, UPDATE_UI);
    }

    private void autoTender(
            ActionEvent actionEvent
    ) {
        final BrowsePrjAutoTenderDlg dlg = new BrowsePrjAutoTenderDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void projectProtocol(
            ActionEvent actionEvent
    ) {
        if (confirm(null, controller().getMessage("protocol-create"))) {
            final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

                @Override
                public String getTitle() {
                    return controller().getMessage("project-protocol");
                }

                @Override
                protected Collection<Map<String, Object>> load() {
                    List<Map<String, Object>> retList = new ArrayList<>();
                    for (int i = 1; i <= 6; i++) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("object-id", id);
                        params.put("index", i);
                        retList.add(params);
                    }
                    return retList;
                }

                @Override
                protected String getCurrent(
                        int i,
                        Map<String, Object> params
                ) {
                    return "生成工程贷协议：" + controller().getMessage("file" + params.get("index"));
                }

                @Override
                protected void execute(
                        int i,
                        Map<String, Object> params
                ) {
                    new ProjectProxy().protocol(params)
                                      .thenApplyAsync(Result::stringValue)
                                      .whenCompleteAsync(this::saveCallback, UPDATE_UI).join();
                }

                private void saveCallback(
                        String string,
                        Throwable t
                ) {
                    if (t != null) {
                        ErrorHandler.handle(t);
                    }
                }

            });
            showModel(null, dlg);
        }
    }

    private void billProtocol(
            ActionEvent actionEvent
    ) {
        if (confirm(null, controller().getMessage("protocol-create"))) {
            final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

                @Override
                public String getTitle() {
                    return controller().getMessage("bill-protocol");
                }

                @Override
                protected Collection<Map<String, Object>> load() {
                    List<Map<String, Object>> retList = new ArrayList<>();
                    for (int i = 4; i <= 9; i++) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("object-id", id);
                        params.put("index", i);
                        retList.add(params);
                    }
                    return retList;
                }

                @Override
                protected String getCurrent(
                        int i,
                        Map<String, Object> params
                ) {
                    return "生成票据贷协议：" + controller().getMessage("file" + params.get("index"));
                }

                @Override
                protected void execute(
                        int i,
                        Map<String, Object> params
                ) {
                    new ProjectProxy().protocol(params)
                                      .thenApplyAsync(Result::stringValue)
                                      .whenCompleteAsync(this::saveCallback, UPDATE_UI).join();
                }

                private void saveCallback(
                        String string,
                        Throwable t
                ) {
                    if (t != null) {
                        ErrorHandler.handle(t);
                    }
                }

            });
            showModel(null, dlg);
        }
    }


    private void checkProtocol(
            ActionEvent actionEvent
    ) {
        final ChooseProtocolDlg dlg = new ChooseProtocolDlg(id, 40, 3);
        dlg.setFixedSize(false);
        showModel(null, dlg);
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
        new ProjectRepayProxy().queryPrjBonus(id)
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
                            .thenApplyAsync(Result::booleanValue)
                            .whenCompleteAsync(this::saveCallback, UPDATE_UI);
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
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
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
        controller().disable("pass");
        final Map<String, Object> params = new HashMap<>();
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
                            .thenApplyAsync(Result::booleanValue)
                            .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                            .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
        }
        doAudit(params);
        this.done(OK);
    }

    public void doAudit(
            Map<String, Object> params
    ) {
        switch (role) {
            case 0:
                new AuditProxy().prjSubmit(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 1:
                new AuditProxy().prjMgrAudit(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 10:
                new AuditProxy().riskCtrlAudit(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 20:
                new AuditProxy().busSecAudit(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 30:
                new AuditProxy().busVpAprOnLine(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 50:
                new AuditProxy().busVpConfirmFull(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 60:
                new AuditProxy().checkFee(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);

                break;
            case 70:
                params.put("s-datepoint", null);
                new AuditProxy().busVpConfirmLoan(params)
                                .thenApplyAsync(Result::booleanValue)
                                .whenCompleteAsync(this::saveCallback, UPDATE_UI)
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
            Boolean flag,
            Throwable t

    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else if (flag) {
            super.done(OK);
        }
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
                controller().show("autoTender");
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
                controller().show("autoTender");
                break;
            case 60:
                controller().setText("role", "财务核收服务费");
                controller().show("tender");
                controller().show("comments");
                controller().show("beizhu");
                controller().show("auctions");
                controller().hide("npass");
                controller().show("projectProtocol");
                controller().show("billProtocol");
                controller().show("checkProtocol");
                controller().show("autoTender");
                break;
            case 70:
                controller().setText("role", "业务副总批准放款");
                controller().show("tender");
                controller().show("comments");
                controller().show("beizhu");
                controller().show("auctions");
                controller().hide("npass");
                controller().show("auctionsPass");
                controller().show("projectProtocol");
                controller().show("billProtocol");
                controller().show("checkProtocol");
                controller().show("autoTender");
                break;
            case 80:
                controller().show("investor");
                controller().show("tender");
                controller().show("loan");
                controller().show("auctions");
                controller().hide("npass");
                controller().hide("pass");
                controller().hide("comments");
                controller().show("projectProtocol");
                controller().show("billProtocol");
                controller().show("checkProtocol");
                controller().show("autoTender");
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
                controller().show("projectProtocol");
                controller().show("billProtocol");
                controller().show("checkProtocol");
                controller().show("autoTender");
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
                controller().show("autoTender");
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
