package com.banhui.console.ui;

import com.banhui.console.rpc.StatisticProxy;
import org.xx.armory.swing.components.DialogPane;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.ui.InputUtils.thisMonth;
import static com.banhui.console.ui.InputUtils.tomorrow;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class StatisticMonthDlg
        extends DialogPane {

    private Map<String, Object> statistic1;
    private Map<String, Object> statistic2;
    private Map<String, Object> statistic3;
    private Map<String, Object> statistic4;
    private Map<String, Object> statistic5;
    private Map<String, Object> statistic6;
    private Map<String, Object> statistic7;
    private Map<String, Object> statistic8;
    private Map<String, Object> statistic9;

    public StatisticMonthDlg() {
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
        html.append("</td><td width='240'>有效投资人数</td><td width='90'>");
        html.append(statistic2.get("validInvestCount"));
        html.append("</td></tr><tr><td>当日注册人数</td><td>");
        html.append(statistic2.get("newRegCount"));
        html.append("</td><td>总注册人数</td><td>");
        html.append(statistic2.get("totalRegCount"));
        html.append("</td></tr><tr><td>当月充值人数(仅投资人)</td><td>");
        html.append(statistic1.get("rechargeCountInvestor"));
        html.append("</td><td>总充值人数(仅投资人)</td><td>");
        html.append(statistic4.get("totalRechargeCountInvestor"));
        html.append("</td></tr><tr><td>当月充值人数(其它)</td><td>");
        html.append(statistic1.get("rechargeCountOther"));
        html.append("</td><td>总充值人数(其它)</td><td>");
        html.append(statistic4.get("totalRechargeCountOther"));
        html.append("</td></tr><tr><td>当月提现人数(仅投资人)</td><td>");
        html.append(statistic6.get("withdrawCountInvestor"));
        html.append("</td><td>总提现人数(仅投资人)</td><td>");
        html.append(statistic7.get("totalWithdrawCountInvestor"));
        html.append("</td></tr><tr><td>当月提现人数(其它)</td><td>");
        html.append(statistic6.get("withdrawCountOther"));
        html.append("</td><td>总提现人数(其它)</td><td>");
        html.append(statistic7.get("totalWithdrawCountOther"));
        html.append("</td></tr><tr><td>当月投资人数</td><td>");
        html.append(statistic2.get("investCount"));
        html.append("</td><td>总投资人数</td><td>");
        html.append(statistic2.get("totalInvestCount"));
        html.append("</td></tr><tr><td>当月借款人数</td><td>");
        html.append(statistic2.get("borrowerCount"));
        html.append("</td><td>总借款人数</td><td>");
        html.append(statistic2.get("totalBorrowerCount"));
        html.append("</td></tr><tr><td>当月充值金额(仅投资人)</td><td>");
        html.append(statistic1.get("rechargeAmtInvestor"));
        html.append("</td><td>总充值金额(仅投资人)</td><td>");
        html.append(statistic4.get("totalRechargeAmtInvestor"));
        html.append("</td></tr><tr><td>当月充值金额(其它)</td><td>");
        html.append(statistic1.get("rechargeAmtOther"));
        html.append("</td><td>总充值金额(其它)</td><td>");
        html.append(statistic4.get("totalRechargeAmtOther"));
        html.append("</td></tr><tr><td>当月提现金额(仅投资人)</td><td>");
        html.append(statistic6.get("withdrawAmtInvestor"));
        html.append("</td><td>总提现金额(仅投资人)</td><td>");
        html.append(statistic7.get("totalWithdrawAmtInvestor"));
        html.append("</td></tr><tr><td>当月提现金额(其它)</td><td>");
        html.append(statistic6.get("withdrawAmtOther"));
        html.append("</td><td>总提现金额(其它)</td><td>");
        html.append(statistic7.get("totalWithdrawAmtOther"));
        html.append("</td></tr><tr><td>当月投资金额</td><td>");
        html.append(statistic3.get("investAmt"));
        html.append("</td><td>总投资金额</td><td>");
        html.append(statistic3.get("totalInvestAmt"));
        html.append("</td></tr><tr><td>当月债权申请笔数</td><td>");
        html.append(statistic9.get("creditApplyCount"));
        html.append("</td><td>总债权申请笔数</td><td>");
        html.append(statistic9.get("totalCreditApplyCount"));
        html.append("</td></tr><tr><td>当月债权成交笔数</td><td>");
        html.append(statistic9.get("creditedCount"));
        html.append("</td><td>总债权成交笔数</td><td>");
        html.append(statistic9.get("totalCreditedCount"));
        html.append("</td></tr><tr><td>当月债权转让成交金额</td><td>");
        html.append(statistic3.get("creditAmt"));
        html.append("</td><td>总债权转让成交金额</td><td>");
        html.append(statistic3.get("totalCreditAmt"));
        html.append("<tr><td>流标金额</td><td>");
        html.append(statistic9.get("cancelTenderAmt"));
        html.append("</td><td>借款余额</td><td>");
        html.append(statistic5.get("loanBalanceAmt"));
        html.append("</td></tr><tr><td>当月还本金额</td><td>");
        html.append(statistic9.get("repayCapitalAmt"));
        html.append("</td><td>总还本金额</td><td>");
        html.append(statistic9.get("totalRepayCapitalAmt"));
        html.append("</td></tr><tr><td>当月还款服务费</td><td>");
        html.append(statistic5.get("repayFeeAmt"));
        html.append("</td><td>总还款服务费</td><td>");
        html.append(statistic5.get("totalRepayFeeAmt"));
        html.append("</td></tr><tr><td>当月债权转让服务费</td><td>");
        html.append(statistic5.get("creditFeeAmt"));
        html.append("</td><td>总债权转让服务费</td><td>");
        html.append(statistic5.get("totalCreditFeeAmt"));
        html.append("</td></tr><tr><td>当月个人平均借款额度</td><td>");
        html.append(statistic8.get("personAverageBorrowAmt"));
        html.append("</td><td>历史个人平均借款总额</td><td>");
        html.append(statistic8.get("totalPersonAverageBorrowAmt"));
        html.append("</td></tr><tr><td>当月机构平均借款额度</td><td>");
        html.append(statistic8.get("orgAverageBorrowAmt"));
        html.append("</td><td>历史机构平均借款总额</td><td>");
        html.append(statistic8.get("totalOrgAverageBorrowAmt"));
        html.append("</td></tr><tr><td>当月上线项目平均借款天数</td><td>");
        html.append(statistic8.get("averageBorrowDays"));
        html.append("</td><td>上线项目平均借款天数</td><td>");
        html.append(statistic8.get("totalAverageBorrowDays"));
        html.append("</td></tr><tr><td>当月上线项目平均借款利率</td><td>");
        html.append(statistic8.get("averageBorrowRate") + "%");
        html.append("</td><td>上线项目平均借款利率</td><td>");
        html.append(statistic8.get("totalAverageBorrowRate") + "%");
        html.append("</td></tr></table>");
        html.append(controller().getMessage("explain"));
        textPane.setText(html.toString());
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        assertUIThread();
        final Map<String, Object> params = new HashMap<>();
        Date datepoint = controller().getDate("datepoint");
        Date date1 = thisMonth(datepoint).getStart();
        Date date2 = tomorrow(thisMonth(datepoint).getEnd()).getStart();
        params.put("datepoint1", date1);
        params.put("datepoint2", date2);

        allDone(new StatisticProxy().monthStatistic1(params),
                new StatisticProxy().monthStatistic2(params),
                new StatisticProxy().monthStatistic3(params),
                new StatisticProxy().monthStatistic4(params),
                new StatisticProxy().monthStatistic5(params),
                new StatisticProxy().monthStatistic6(params),
                new StatisticProxy().monthStatistic7(params),
                new StatisticProxy().monthStatistic8(params),
                new StatisticProxy().monthStatistic9(params)
        ).thenApply(results -> new Object[]{
                results[0].map(),
                results[1].map(),
                results[2].map(),
                results[3].map(),
                results[4].map(),
                results[5].map(),
                results[6].map(),
                results[7].map(),
                results[8].map()
        }).whenCompleteAsync(this::searchCallback, UPDATE_UI)
         .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
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
            statistic1.put("rechargeAmtInvestor", to2Scale(result0, "rechargeAmtInvestor"));
            statistic1.put("rechargeAmtOther", to2Scale(result0, "rechargeAmtOther"));

            final Map<String, Object> result1 = (Map<String, Object>) results[1];
            statistic2.put("newRegCount", intValue(result1, "newRegCount"));
            statistic2.put("validInvestCount", intValue(result1, "validInvestCount"));
            statistic2.put("totalRegCount", intValue(result1, "totalRegCount"));
            statistic2.put("newInvestCount", intValue(result1, "newInvestCount"));
            statistic2.put("investCount", intValue(result1, "investCount"));
            statistic2.put("totalInvestCount", intValue(result1, "totalInvestCount"));
            statistic2.put("borrowerCount", intValue(result1, "borrowerCount"));
            statistic2.put("totalBorrowerCount", intValue(result1, "totalBorrowerCount"));

            final Map<String, Object> result2 = (Map<String, Object>) results[2];
            statistic3.put("investAmt", to2Scale(result2, "investAmt"));
            statistic3.put("creditAmt", to2Scale(result2, "creditAmt"));
            statistic3.put("totalInvestAmt", to2Scale(result2, "totalInvestAmt"));
            statistic3.put("totalCreditAmt", to2Scale(result2, "totalCreditAmt"));

            final Map<String, Object> result3 = (Map<String, Object>) results[3];
            statistic4.put("totalRechargeCountInvestor", intValue(result3, "totalRechargeCountInvestor"));
            statistic4.put("totalRechargeCountOther", intValue(result3, "totalRechargeCountOther"));
            statistic4.put("totalRechargeAmtInvestor", to2Scale(result3, "totalRechargeAmtInvestor"));
            statistic4.put("totalRechargeAmtOther", to2Scale(result3, "totalRechargeAmtOther"));

            final Map<String, Object> result4 = (Map<String, Object>) results[4];
            statistic5.put("loanBalanceAmt", to2Scale(result4, "loanBalanceAmt"));
            statistic5.put("repayFeeAmt", to2Scale(result4, "repayFeeAmt"));
            statistic5.put("totalRepayFeeAmt", to2Scale(result4, "totalRepayFeeAmt"));
            statistic5.put("creditFeeAmt", to2Scale(result4, "creditFeeAmt"));
            statistic5.put("totalCreditFeeAmt", to2Scale(result4, "creditFeeAmt"));

            final Map<String, Object> result5 = (Map<String, Object>) results[5];
            statistic6.put("withdrawCountInvestor", intValue(result5, "withdrawCountInvestor"));
            statistic6.put("withdrawCountOther", intValue(result5, "withdrawCountOther"));
            statistic6.put("withdrawAmtInvestor", to2Scale(result5, "withdrawAmtInvestor"));
            statistic6.put("withdrawAmtOther", to2Scale(result5, "withdrawAmtOther"));

            final Map<String, Object> result6 = (Map<String, Object>) results[6];
            statistic7.put("totalWithdrawCountInvestor", intValue(result6, "totalWithdrawCountInvestor"));
            statistic7.put("totalWithdrawCountOther", intValue(result6, "totalWithdrawCountOther"));
            statistic7.put("totalWithdrawAmtInvestor", to2Scale(result6, "totalWithdrawAmtInvestor"));
            statistic7.put("totalWithdrawAmtOther", to2Scale(result6, "totalWithdrawAmtOther"));

            final Map<String, Object> result7 = (Map<String, Object>) results[7];
            statistic8.put("averageBorrowDays", intValue(result7, "averageBorrowDays"));
            statistic8.put("totalAverageBorrowDays", intValue(result7, "totalAverageBorrowDays"));
            statistic8.put("averageBorrowRate", to2Scale(result7, "averageBorrowRate"));
            statistic8.put("totalAverageBorrowRate", to2Scale(result7, "totalAverageBorrowRate"));
            statistic8.put("personAverageBorrowAmt", to2Scale(result7, "personAverageBorrowAmt"));
            statistic8.put("totalPersonAverageBorrowAmt", to2Scale(result7, "totalPersonAverageBorrowAmt"));
            statistic8.put("orgAverageBorrowAmt", to2Scale(result7, "orgAverageBorrowAmt"));
            statistic8.put("totalOrgAverageBorrowAmt", to2Scale(result7, "totalOrgAverageBorrowAmt"));

            final Map<String, Object> result8 = (Map<String, Object>) results[8];
            statistic9.put("repayCapitalAmt", to2Scale(result8, "repayCapitalAmt"));
            statistic9.put("totalRepayCapitalAmt", to2Scale(result8, "totalRepayCapitalAmt"));
            statistic9.put("creditApplyCount", intValue(result8, "creditApplyCount"));
            statistic9.put("totalCreditApplyCount", intValue(result8, "totalCreditApplyCount"));
            statistic9.put("creditedCount", intValue(result8, "creditedCount"));
            statistic9.put("totalCreditedCount", intValue(result8, "totalCreditedCount"));
            statistic9.put("cancelTenderAmt", to2Scale(result8, "cancelTenderAmt"));

            doHtml();
        }
    }

    private BigDecimal to2Scale(
            Map<String, Object> data,
            String key
    ) {
        BigDecimal num = decimalValue(data, key);
        if (num != null) {
            return num.setScale(2, RoundingMode.HALF_UP);
        } else {
            return new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        }
    }

    private void initMap() {
        statistic1 = new HashMap<>();
        statistic1.put("rechargeCountInvestor", "");
        statistic1.put("rechargeCountOther", "");
        statistic1.put("rechargeAmtInvestor", "");
        statistic1.put("rechargeAmtOther", "");

        statistic2 = new HashMap<>();
        statistic2.put("newInvestCount", "");
        statistic2.put("validInvestCount", "");
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
        statistic4.put("totalRechargeAmtInvestor", "");
        statistic4.put("totalRechargeAmtOther", "");

        statistic5 = new HashMap<>();
        statistic5.put("loanBalanceAmt", "");
        statistic5.put("repayFeeAmt", "");
        statistic5.put("totalRepayFeeAmt", "");
        statistic5.put("creditFeeAmt", "");
        statistic5.put("totalCreditFeeAmt", "");

        statistic6 = new HashMap<>();
        statistic6.put("withdrawCountInvestor", "");
        statistic6.put("withdrawCountOther", "");
        statistic6.put("withdrawAmtInvestor", "");
        statistic6.put("withdrawAmtOther", "");

        statistic7 = new HashMap<>();
        statistic7.put("totalWithdrawCountInvestor", "");
        statistic7.put("totalWithdrawCountOther", "");
        statistic7.put("totalWithdrawAmtInvestor", "");
        statistic7.put("totalWithdrawAmtOther", "");

        statistic8 = new HashMap<>();
        statistic8.put("averageBorrowDays", "");
        statistic8.put("totalAverageBorrowDays", "");
        statistic8.put("averageBorrowRate", "");
        statistic8.put("totalAverageBorrowRate", "");
        statistic8.put("personAverageBorrowAmt", "");
        statistic8.put("totalPersonAverageBorrowAmt", "");
        statistic8.put("orgAverageBorrowAmt", "");
        statistic8.put("totalOrgAverageBorrowAmt", "");

        statistic9 = new HashMap<>();
        statistic9.put("repayCapitalAmt", "");
        statistic9.put("totalRepayCapitalAmt", "");
        statistic9.put("creditApplyCount", "");
        statistic9.put("totalCreditApplyCount", "");
        statistic9.put("creditedCount", "");
        statistic9.put("totalCreditedCount", "");
        statistic9.put("cancelTenderAmt", "");
    }
}
