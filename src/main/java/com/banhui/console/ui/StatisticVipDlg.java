package com.banhui.console.ui;

import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.StatisticProxy;
import org.xx.armory.swing.components.DialogPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.DialogUtils.prompt;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class StatisticVipDlg
        extends DialogPane {

    public StatisticVipDlg() {
        controller().setNumber("limit-amt", 50000L);
        controller().setDate("datepoint", new Date());
        controller().connect("search", this::search);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        final Map<String, Object> params = new HashMap<>();
        Date datepoint = controller().getDate("datepoint");
        BigDecimal limitAmt = controller().getDecimal("limit-amt");
        params.put("datepoint", datepoint);
        params.put("limit-amt", limitAmt);
        new StatisticProxy().dataOfVip(params)
                            .thenApplyAsync(Result::list)
                            .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                            .exceptionally(ErrorHandler::handle)
                            .thenAcceptAsync(v -> controller().enable("search"), UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c
    ) {
        final JTextPane textPane = controller().get(JTextPane.class, "list");
        textPane.setContentType("text/html");
        textPane.setEditable(false);

        StringBuffer html = new StringBuffer();
        html.append(controller().getMessage("css"));
        html.append(controller().getMessage("title"));
        if (c.size() == 0) {
            html.append("</table></body></html>");
            textPane.setText(html.toString());
            prompt(this.getOwner(), controller().getMessage("null-text"));
            return;
        }
        for (Map<String, Object> data : c) {
            html.append("<tr><td>");
            html.append(intValue(data, "auId"));
            html.append("</td><td>");
            html.append(stringValue(data, "loginName"));
            html.append("</td><td>");
            html.append(stringValue(data, "realName"));
            html.append("</td><td>");
            html.append(stringValue(data, "uName"));
            html.append("</td><td>");
            html.append(stringValue(data, "mobile"));
            html.append("</td><td>");
            html.append(decimalValue(data, "monthSumAmt"));
            html.append("</td><td>");
            html.append(decimalValue(data, "blanceAmt"));
            html.append("</td><td>");
            html.append(decimalValue(data, "monthSumCredit"));
        }
        html.append("</td></tr></table></body></html>");
        textPane.setText(html.toString());
    }
}
