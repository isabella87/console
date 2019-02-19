package com.banhui.console.ui;

import com.banhui.console.rpc.AuditProxy;
import com.banhui.console.rpc.AuthenticationProxy;
import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.ProjectRepayProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.fail;
import static org.xx.armory.swing.DialogUtils.inputPassword;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;


public class AuditProjectsDlg extends DialogPane {

    private volatile long id;
    private volatile int role;
    private Map<String, Object> row;
    private Map<String, Object> auditParam;
    private volatile double amt;
    private volatile double investedAmt;
    private volatile boolean lockStatus;

    public AuditProjectsDlg(
            long id,
            long status,
            double amt,
            double investedAmt
    ) {
        this.id = id;
        role = (int) status;
        this.amt = amt;
        this.investedAmt = investedAmt;
        setTitle(getTitle() + id);

        JLabel perAmtLable = controller().get(JLabel.class, "investedAmt");
        perAmtLable.setForeground(Color.red);
        JLabel roleSuffixInfoLable = controller().get(JLabel.class, "role-suffix-info");
        roleSuffixInfoLable.setForeground(Color.red);

        controller().connect("npass", this::npass);
        controller().connect("auctionsPass", this::auctionsPass);
        controller().connect("pass", this::pass);
        controller().connect("auctions", this::auctions);
        controller().connect("tender", this::tender);
        controller().connect("investor", this::investor);
        controller().connect("loan", this::loan);
        controller().connect("investment", this::investment);
        controller().connect("completed", this::completed);
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
        controller().hide("projectProtocol");
        controller().hide("billProtocol");
        controller().hide("checkProtocol");
        controller().hide("autoTender");
        controller().hide("investedAmt");
        controller().hide("role-suffix-info");
        controller().hide("immedit-online-flag");
        controller().hide("immedit-online-flag-mes");
        if (role == 20) {
            controller().show("immedit-online-flag");
            controller().show("immedit-online-flag-mes");
        }

        new AuditProxy().queryAudit(id)
                        .thenApplyAsync(Result::list)
                        .whenCompleteAsync(this::searchCallback, UPDATE_UI);

        getUi();
        new AuditProxy().prjInvestorNum(id)
                        .thenApplyAsync(Result::map)
                        .whenCompleteAsync(this::prjInvestorNumCallback,UPDATE_UI);

        new AuditProxy().prjLockStatus(id)
                        .thenApplyAsync(Result::booleanValue)
                        .whenCompleteAsync(this::lockStatus, UPDATE_UI);


    }

    private void prjInvestorNumCallback(
            Map<String, Object> prjInvestors,
            Throwable throwable
    ) {
        if(throwable != null){
            ErrorHandler.handle(throwable);
        }else{
            if(role == 50) {
                StringBuffer roleSuffixInfo = new StringBuffer();
                if (longValue(prjInvestors, "userType") == 1 && longValue(prjInvestors, "investorNum") > 29) {
                    roleSuffixInfo.append("  最多允许29个出借人，目前已有").append(longValue(prjInvestors, "investorNum")).append("个出借人。");

                }else if (longValue(prjInvestors, "userType") == 2 && longValue(prjInvestors, "investorNum") > 149) {
                    roleSuffixInfo.append("  最多允许149个出借人，目前已有").append(longValue(prjInvestors, "investorNum")).append("个出借人。");
                }else{
                    roleSuffixInfo.append("  测试环境下你能看到我~最多允许XXX个出借人，目前已有").append(longValue(prjInvestors, "investorNum")).append("个出借人。");
                }

                if (roleSuffixInfo.toString().length() > 0) {
                    controller().show("role-suffix-info");
                    controller().setText("role-suffix-info", roleSuffixInfo.toString());
                }
            }
        }
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
            Boolean status,
            Throwable t
    ) {
        this.lockStatus = status;
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            if (status) {
                controller().hide("pass");
                controller().hide("npass");
                controller().hide("auctions");
                controller().hide("auctionsPass");
                controller().hide("completed");
                controller().hide("beizhu");
                controller().hide("comments");
            }
        }
    }

    //结清
    private void completed(ActionEvent actionEvent) {
        new ProjectRepayProxy().queryPrjBonus(id)
                               .thenApplyAsync(Result::list)
                               .whenCompleteAsync(this::updateUnPaidAmt, UPDATE_UI);
    }

    private void updateUnPaidAmt(
            Collection<Map<String, Object>> rows,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
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
        final BrowsePrjLoanDlg dlg = new BrowsePrjLoanDlg(id, role);
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
        final BrowsePrjTenderDlg dlg = new BrowsePrjTenderDlg(id, role, lockStatus);
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
        final Map<String, Object> params = new HashMap<>();
        if (this.id != 0) {
            params.put("p-id", id);
        }
        params.put("flag", true);
        params.put("comments", controller().getText("comments").trim());
        params.put("signature", null);
        this.auditParam = params;
        if (role == 60 || role == 70) {
            checkPwd();
        } else {
            doAudit(params);
            this.done(OK);
        }
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
                if (controller().getBoolean("immedit-online-flag")) {
                    new AuditProxy().busVpAprOnLine(params)
                                    .thenApplyAsync(Result::booleanValue)
                                    .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                    .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                } else {
                    new AuditProxy().busSecAudit(params)
                                    .thenApplyAsync(Result::booleanValue)
                                    .whenCompleteAsync(this::saveCallback, UPDATE_UI)
                                    .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                }
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
            Collection<Map<String, Object>> c,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(c);
        }
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
                controller().setText("role", "业务副总审批上线");
                controller().show("comments");
                controller().show("beizhu");
                break;
            case 30:
                controller().setText("role", "业务副总批准立刻上线");
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
                if (investedAmt > amt) {
                    controller().show("investedAmt");
                    controller().setText("investedAmt", controller().formatMessage("amt", amt, investedAmt));
                }
                controller().show("tender");
                controller().show("comments");
                controller().show("beizhu");
                controller().show("auctions");
                controller().hide("npass");
                controller().show("auctionsPass");
                controller().show("autoTender");
                break;
            case 60:
                controller().setText("role", "风控复核");
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

    public void checkPwd() {
        String inputPwd = inputPassword(getOwner(), "操作人员身份验证：输入登录密码", "");
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
                doAudit(auditParam);
                this.done(OK);
            } else {
                fail(getOwner(), controller().getMessage("pwd-fail"));
            }
        }
    }
}
