package com.banhui.console.ui;

import com.banhui.console.rpc.StatisticProxy;
import org.xx.armory.swing.components.DialogPane;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static com.banhui.console.ui.InputUtils.tomorrow;

public class StatisticDailyDlg
        extends DialogPane {

    private Map<String, Object> statistic1;
    private Map<String, Object> statistic2;
    private Map<String, Object> statistic3;
    private Map<String, Object> statistic4;
    private Map<String, Object> statistic5;

    public StatisticDailyDlg() {
        initMap();

        doHtml();

        controller().setDate("datepoint", new Date());
        controller().connect("search", this::search);
    }

    private void doHtml() {

        final JTextPane textPane = controller().get(JTextPane.class, "list");
        textPane.setContentType("text/html");
        textPane.setEditable(false);

        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        StringBuffer html = new StringBuffer();
        html.append(controller().getMessage("css"));
        html.append(controller().getMessage("unit"));
        html.append("<table><tr><td width='240'>新增投资人数</td><td width='90'>");
        html.append(statistic2.get("newInvestCount"));
        html.append("</td><td width='240'></td><td width='90'></td></tr>");
        html.append("<tr><td>当日注册人数</td><td>");
        html.append(statistic2.get("newRegCount"));
        html.append("</td><td>总注册人数</td><td>");
        html.append(statistic2.get("totalRegCount"));
        html.append("</td></tr><tr><td>当日充值人数(仅投资人)</td><td>");
        html.append(statistic1.get("rechargeCountInvestor"));
        html.append("</td><td>总充值人数(仅投资人)</td><td>");
        html.append(statistic4.get("totalRechargeCountInvestor"));
        html.append("</td></tr><tr><td>当日充值人数(其它)</td><td>");
        html.append(statistic1.get("rechargeCountOther"));
        html.append("</td><td>总充值人数(其它)</td><td>");
        html.append(statistic4.get("totalRechargeCountOther"));
        html.append("</td></tr><tr><td>当日提现人数(仅投资人)</td><td>");
        html.append(statistic1.get("withdrawCountInvestor"));
        html.append("</td><td>总提现人数(仅投资人)</td><td>");
        html.append(statistic4.get("totalWithdrawCountInvestor"));
        html.append("</td></tr><tr><td>当日提现人数(其它)</td><td>");
        html.append(statistic1.get("withdrawCountOther"));
        html.append("</td><td>总提现人数(其它)</td><td>");
        html.append(statistic4.get("totalWithdrawCountOther"));
        html.append("</td></tr><tr><td>当日投资人数</td><td>");
        html.append(statistic2.get("investCount"));
        html.append("</td><td>总投资人数</td><td>");
        html.append(statistic2.get("totalInvestCount"));
        html.append("</td></tr><tr><td>当日充值金额(仅投资人)</td><td>");
        html.append(statistic1.get("rechargeAmtInvestor"));
        html.append("</td><td>总充值金额(仅投资人)</td><td>");
        html.append(statistic4.get("totalRechargeAmtInvestor"));
        html.append("</td></tr><tr><td>当日充值金额(其它)</td><td>");
        html.append(statistic1.get("rechargeAmtOther"));
        html.append("</td><td>总充值金额(其它)</td><td>");
        html.append(statistic4.get("totalRechargeAmtOther"));
        html.append("</td></tr><tr><td>当日提现金额(仅投资人)</td><td>");
        html.append(statistic1.get("withdrawAmtInvestor"));
        html.append("</td><td>总提现金额(仅投资人)</td><td>");
        html.append(statistic4.get("totalWithdrawAmtInvestor"));
        html.append("</td></tr><tr><td>当日提现金额(其它)</td><td>");
        html.append(statistic1.get("withdrawAmtOther"));
        html.append("</td><td>总提现金额(其它)</td><td>");
        html.append(statistic4.get("totalWithdrawAmtOther"));
        html.append("</td></tr><tr><td>当日投资金额</td><td>");
        html.append(statistic3.get("investAmt"));
        html.append("</td><td>总投资金额</td><td>");
        html.append(statistic3.get("totalInvestAmt"));
        html.append("</td></tr><tr><td>当日债权转让成交金额</td><td>");
        html.append(statistic3.get("creditAmt"));
        html.append("</td><td>总债权转让成交金额</td><td>");
        html.append(statistic3.get("totalCreditAmt"));
        html.append("<tr><td></td><td></td><td>借款余额</td><td>");
        html.append(statistic5.get("loanBalanceAmt"));
        html.append("</td></tr><tr><td>当日还本金额</td><td>");
        html.append(statistic5.get("repayCapitalAmt"));
        html.append("</td><td>总还本金额</td><td>");
        html.append(statistic5.get("totalRepayCapitalAmt"));
        html.append("</td></tr><tr><td>当日还款服务费</td><td>");
        html.append(statistic5.get("repayFeeAmt"));
        html.append("</td><td>总还款服务费</td><td>");
        html.append(statistic5.get("totalRepayFeeAmt"));
        html.append("</td></tr><tr><td>当日债权转让服务费</td><td>");
        html.append(statistic5.get("creditFeeAmt"));
        html.append("</td><td>总债权转让服务费</td><td>");
        html.append(statistic5.get("totalCreditFeeAmt"));
        html.append("</td></tr><tr><td>当日个人平均借款额度</td><td>");
        html.append(statistic5.get("personAverageBorrowAmt"));
        html.append("</td><td>历史个人平均借款总额</td><td>");
        html.append(statistic5.get("totalPersonAverageBorrowAmt"));
        html.append("</td></tr><tr><td>当日机构平均借款额度</td><td>");
        html.append(statistic5.get("orgAverageBorrowAmt"));
        html.append("</td><td>历史机构平均借款总额</td><td>");
        html.append(statistic5.get("totalOrgAverageBorrowAmt"));
        html.append("</td></tr></table>");
        html.append(controller().getMessage("explain"));
        textPane.setText(html.toString());
    }

    private void search(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        final Map<String, Object> params = new HashMap<>();
        Date datepoint = controller().getDate("datepoint");
        Date date = tomorrow(datepoint).getStart();
        params.put("datepoint", date);
        allDone(new StatisticProxy().dailyStatistic1(params),
                new StatisticProxy().dailyStatistic2(params),
                new StatisticProxy().dailyStatistic3(params),
                new StatisticProxy().dailyStatistic4(params),
                new StatisticProxy().dailyStatistic5(params)
        ).thenApply(results -> new Object[]{
                results[0].map(),
                results[1].map(),
                results[2].map(),
                results[3].map(),
                results[4].map()
        }).whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void searchCallback(
            Object[] results,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final Map<String, Object> result0 = (Map<String, Object>) results[0];
            statistic1.put("rechargeCountInvestor", intValue(result0, "rechargeCountInvestor"));
            statistic1.put("rechargeCountOther", intValue(result0, "rechargeCountOther"));
            statistic1.put("withdrawCountInvestor", intValue(result0, "withdrawCountInvestor"));
            statistic1.put("withdrawCountOther", intValue(result0, "withdrawCountOther"));
            statistic1.put("rechargeAmtInvestor", decimalValue(result0, "rechargeAmtInvestor"));
            statistic1.put("rechargeAmtOther", decimalValue(result0, "rechargeAmtOther"));
            statistic1.put("withdrawAmtInvestor", decimalValue(result0, "withdrawAmtInvestor"));
            statistic1.put("withdrawAmtOther", decimalValue(result0, "withdrawAmtOther"));

            final Map<String, Object> result1 = (Map<String, Object>) results[1];
            statistic2.put("newRegCount", intValue(result1, "newRegCount"));
            statistic2.put("totalRegCount", intValue(result1, "totalRegCount"));
            statistic2.put("newInvestCount", intValue(result1, "newInvestCount"));
            statistic2.put("investCount", intValue(result1, "investCount"));
            statistic2.put("totalInvestCount", intValue(result1, "totalInvestCount"));
            statistic2.put("borrowerCount", intValue(result1, "borrowerCount"));
            statistic2.put("totalBorrowerCount", intValue(result1, "totalBorrowerCount"));

            final Map<String, Object> result2 = (Map<String, Object>) results[2];
            statistic3.put("investAmt", decimalValue(result2, "investAmt"));
            statistic3.put("creditAmt", decimalValue(result2, "creditAmt"));
            statistic3.put("totalInvestAmt", decimalValue(result2, "totalInvestAmt"));
            statistic3.put("totalCreditAmt", decimalValue(result2, "totalCreditAmt"));

            final Map<String, Object> result3 = (Map<String, Object>) results[3];
            statistic4.put("totalRechargeCountInvestor", intValue(result3, "totalRechargeCountInvestor"));
            statistic4.put("totalRechargeCountOther", intValue(result3, "totalRechargeCountOther"));
            statistic4.put("totalWithdrawCountInvestor", intValue(result3, "totalWithdrawCountInvestor"));
            statistic4.put("totalWithdrawCountOther", intValue(result3, "totalWithdrawCountOther"));
            statistic4.put("totalRechargeAmtInvestor", decimalValue(result3, "totalRechargeAmtInvestor"));
            statistic4.put("totalRechargeAmtOther", decimalValue(result3, "totalRechargeAmtOther"));
            statistic4.put("totalWithdrawAmtInvestor", decimalValue(result3, "totalWithdrawAmtInvestor"));
            statistic4.put("totalWithdrawAmtOther", decimalValue(result3, "totalWithdrawAmtOther"));

            final Map<String, Object> result4 = (Map<String, Object>) results[4];
            statistic5.put("loanBalanceAmt", decimalValue(result4, "loanBalanceAmt"));
            statistic5.put("repayFeeAmt", decimalValue(result4, "repayFeeAmt"));
            statistic5.put("totalRepayFeeAmt", decimalValue(result4, "totalRepayFeeAmt"));
            statistic5.put("creditFeeAmt", decimalValue(result4, "creditFeeAmt"));
            statistic5.put("totalCreditFeeAmt", decimalValue(result4, "totalCreditFeeAmt"));
            statistic5.put("averageBorrowDays", decimalValue(result4, "averageBorrowDays"));
            statistic5.put("totalAverageBorrowDays", decimalValue(result4, "totalAverageBorrowDays"));
            statistic5.put("averageBorrowRate", decimalValue(result4, "averageBorrowRate") + "%");
            statistic5.put("totalAverageBorrowRate", decimalValue(result4, "totalAverageBorrowRate") + "%");
            statistic5.put("personAverageBorrowAmt", decimalValue(result4, "personAverageBorrowAmt"));
            statistic5.put("totalPersonAverageBorrowAmt", decimalValue(result4, "totalPersonAverageBorrowAmt"));
            statistic5.put("orgAverageBorrowAmt", decimalValue(result4, "orgAverageBorrowAmt"));
            statistic5.put("totalOrgAverageBorrowAmt", decimalValue(result4, "totalOrgAverageBorrowAmt"));
            statistic5.put("repayCapitalAmt", decimalValue(result4, "repayCapitalAmt"));
            statistic5.put("totalRepayCapitalAmt", decimalValue(result4, "totalRepayCapitalAmt"));

            doHtml();
        }
    }

    private void initMap() {
        statistic1 = new HashMap<>();
        statistic1.put("rechargeCountInvestor", "");
        statistic1.put("rechargeCountOther", "");
        statistic1.put("withdrawCountInvestor", "");
        statistic1.put("withdrawCountOther", "");
        statistic1.put("rechargeAmtInvestor", "");
        statistic1.put("rechargeAmtOther", "");
        statistic1.put("withdrawAmtInvestor", "");
        statistic1.put("withdrawAmtOther", "");

        statistic2 = new HashMap<>();
        statistic2.put("newInvestCount", "");
        statistic2.put("investCount", "");
        statistic2.put("newRegCount", "");
        statistic2.put("totalRegCount", "");
        statistic2.put("totalInvestCount", "");
        statistic2.put("borrowerCount", "");
        statistic2.put("totalBorrowerCount", "");

        statistic3 = new HashMap<>();
        statistic3.put("investAmt", "");
        statistic3.put("creditAmt", "");
        statistic3.put("totalInvestAmt", "");
        statistic3.put("totalCreditAmt", "");

        statistic4 = new HashMap<>();
        statistic4.put("totalRechargeCountInvestor", "");
        statistic4.put("totalRechargeCountOther", "");
        statistic4.put("totalWithdrawCountInvestor", "");
        statistic4.put("totalWithdrawCountOther", "");
        statistic4.put("totalRechargeAmtInvestor", "");
        statistic4.put("totalRechargeAmtOther", "");
        statistic4.put("totalWithdrawAmtInvestor", "");
        statistic4.put("totalWithdrawAmtOther", "");

        statistic5 = new HashMap<>();
        statistic5.put("loanBalanceAmt", "");
        statistic5.put("repayFeeAmt", "");
        statistic5.put("totalRepayFeeAmt", "");
        statistic5.put("creditFeeAmt", "");
        statistic5.put("totalCreditFeeAmt", "");
        statistic5.put("averageBorrowDays", "");
        statistic5.put("totalAverageBorrowDays", "");
        statistic5.put("averageBorrowRate", "");
        statistic5.put("totalAverageBorrowRate", "");
        statistic5.put("personAverageBorrowAmt", "");
        statistic5.put("totalPersonAverageBorrowAmt", "");
        statistic5.put("orgAverageBorrowAmt", "");
        statistic5.put("totalOrgAverageBorrowAmt", "");
        statistic5.put("repayCapitalAmt", "");
        statistic5.put("totalRepayCapitalAmt", "");
    }
}
