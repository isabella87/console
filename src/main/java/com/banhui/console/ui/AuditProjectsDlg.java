package com.banhui.console.ui;


import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

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
        setTitle(controller().getMessage("audit") + "-" + id);
        new ProjectProxy().queryAudit(id)
                          .thenApplyAsync(Result::list)
                          .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                          .exceptionally(ErrorHandler::handle);
        getUi();
        new ProjectProxy().prjLockStatus(id)
                          .thenApplyAsync(Result::longValue)
                          .thenAcceptAsync(this::lockStatus, UPDATE_UI);
    }

    @Override
    protected void initUi() {
        super.initUi();
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
    }

    private void lockStatus(
            OptionalLong status
    ) {
        if (status.getAsLong() == 1) {
            controller().hide("pass");
            controller().hide("npass");
            controller().hide("auctions");
            controller().hide("auctionsPass");
            controller().hide("completed");
            controller().hide("lock");
            controller().hide("beizhu");
            controller().hide("comments");
        } else {
            controller().show("lock");
            controller().hide("unlock");
        }
    }

    //锁定
    private void lock(
            ActionEvent actionEvent
    ) {
        new ProjectProxy().lockPrj(id)
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
        new ProjectProxy().unlockPrj(id)
                          .thenApplyAsync(Result::map);
        controller().show("lock");
        controller().hide("unlock");
        getUi();
    }

    //结清
    private void completed(ActionEvent actionEvent) {

        new ProjectProxy().prjBonus(id)
                          .thenApplyAsync(Result::list)
                          .thenAcceptAsync(this::updateUnPaidAmt, UPDATE_UI)
                          .exceptionally(ErrorHandler::handle);
    }

    private void updateUnPaidAmt(Collection<Map<String, Object>> row) {
        BigDecimal unPaidAmt = BigDecimal.ZERO;
        if (row == null || row.isEmpty()) {
            unPaidAmt = BigDecimal.ZERO;
        } else {
            for (Map<String, Object> item : row) {
                unPaidAmt = unPaidAmt.add(decimalValue(item, "unpaidAmt"));
            }
        }
        String confirmClose = controller().formatMessage("confirm-close", unPaidAmt);
        String confirmCompleted = controller().formatMessage("confirm-completed");

        if (confirm(confirmClose, confirmCompleted)) {
            new ProjectProxy().completedPrj(id)
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
        showModel(Application.mainFrame(), dlg);
    }

    //查看放款信息
    private void loan(
            ActionEvent actionEvent
    ) {
        final BrowsePrjLoanDlg dlg = new BrowsePrjLoanDlg(id);
        dlg.setFixedSize(false);
        showModel(Application.mainFrame(), dlg);
    }

    //生成出借人信息表
    private void investor(
            ActionEvent actionEvent
    ) {
        final BrowsePrjInvestorsDlg dlg = new BrowsePrjInvestorsDlg(id);
        dlg.setFixedSize(false);
        showModel(Application.mainFrame(), dlg);
    }

    //查看投标
    private void tender(
            ActionEvent actionEvent
    ) {
        final BrowsePrjTenderDlg dlg = new BrowsePrjTenderDlg(id, role);
        dlg.setFixedSize(false);
        showModel(Application.mainFrame(), dlg);
    }

    //执行流标
    private void auctions(
            ActionEvent actionEvent
    ) {
        final BrowsePrjCancelTendersDlg dlg = new BrowsePrjCancelTendersDlg(id);
        dlg.setFixedSize(false);
        showModel(Application.mainFrame(), dlg);
    }


    //不通过并且流标
    private void auctionsPass(
            ActionEvent actionEvent
    ) {
        final String comments = controller().getText("comments").trim();
        if (comments.equals("") || comments.equals(null)) {
            String confirmNpassBlank = controller().formatMessage("confirm-npass-blank");
            fail(confirmNpassBlank);
        } else {
            String confirmAuctionsPassText = controller().formatMessage("confirm-auctionsPass-text");
            String confirmAuctionsPass = controller().formatMessage("confirm-auctionsPass");
            if (confirm(confirmAuctionsPassText, confirmAuctionsPass)) {
                final Map<String, Object> params = new HashMap<>();
                controller().disable("auctions");
                if (this.id != 0) {
                    params.put("p-id", id);
                }
                params.put("comments", controller().getText("comments").trim());
                new ProjectProxy().busVpStopRaising(params, this.id)
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
        if (comments.equals("") || comments.equals(null)) {
            String confirmNpassBlank = controller().formatMessage("confirm-npass-blank");
            fail(confirmNpassBlank);
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
            String bondText = inputText("输入财务核收服务费", "0");
            long bond;
            if (bondText != null) {
                bond = Long.valueOf(bondText);
            } else {
                return;
            }
            final Map<String, Object> params2 = new HashMap<>();
            params2.put("bond_amt", bond);
            new ProjectProxy().updatePrjBond(params2, this.id)
                              .thenApplyAsync(Result::map)
                              .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                              .exceptionally(ErrorHandler::handle)
                              .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
        }
        doAudit(params);
    }

    public void doAudit(
            Map<String, Object> params
    ) {
        switch (role) {
            case 0:
                new ProjectProxy().prjSubmit(params, this.id)
                                  .thenApplyAsync(Result::map)
                                  .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                  .exceptionally(ErrorHandler::handle)
                                  .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 1:
                new ProjectProxy().prjMgrAudit(params, this.id)
                                  .thenApplyAsync(Result::map)
                                  .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                  .exceptionally(ErrorHandler::handle)
                                  .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 10:
                new ProjectProxy().riskCtrlAudit(params, this.id)
                                  .thenApplyAsync(Result::map)
                                  .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                  .exceptionally(ErrorHandler::handle)
                                  .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 20:
                new ProjectProxy().busSecAudit(params, this.id)
                                  .thenApplyAsync(Result::map)
                                  .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                  .exceptionally(ErrorHandler::handle)
                                  .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 30:
                new ProjectProxy().busVpAprOnLine(params, this.id)
                                  .thenApplyAsync(Result::map)
                                  .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                  .exceptionally(ErrorHandler::handle)
                                  .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 50:
                new ProjectProxy().busVpConfirmFull(params, this.id)
                                  .thenApplyAsync(Result::map)
                                  .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                  .exceptionally(ErrorHandler::handle)
                                  .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);
                break;
            case 60:
                new ProjectProxy().checkFee(params, this.id)
                                  .thenApplyAsync(Result::map)
                                  .thenAcceptAsync(this::saveCallback, UPDATE_UI)
                                  .exceptionally(ErrorHandler::handle)
                                  .thenAcceptAsync(v -> controller().enable("pass"), UPDATE_UI);

                break;
            case 70:
                params.put("s-datepoint", null);
                new ProjectProxy().busVpConfirmLoan(params, this.id)
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
