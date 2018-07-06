package com.banhui.console.ui;

import com.banhui.console.rpc.Result;
import com.banhui.console.rpc.StatisticProxy;
import org.xx.armory.swing.components.DialogPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;
import static org.xx.armory.swing.DialogUtils.prompt;

public class StatisticNewInvestorsDlg
        extends DialogPane {

    public StatisticNewInvestorsDlg() {
        controller().setDate("datepoint", new Date());
        controller().connect("search", this::search);
    }

    private void search(
            ActionEvent actionEvent
    ) {
        assertUIThread();
        final Map<String, Object> params = new HashMap<>();
        Date datepoint = controller().getDate("datepoint");
        params.put("datepoint", datepoint);

        new StatisticProxy().newInvestors(params)
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
            html.append(stringValue(data, "mobile"));
            html.append("</td><td>");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            html.append(sdf.format(dateValue(data, "datepoint")));
            html.append("</td><td>");
            html.append(decimalValue(data, "amt"));
            html.append("</td><td>");
            html.append(stringValue(data, "recommendRealName"));
            html.append("</td><td>");
            html.append(stringValue(data, "recommendMobile"));
            html.append("</td><td>");
            html.append(decimalValue(data, "recommendAmt"));
        }
        html.append("</td></tr></table></body></html>");
        textPane.setText(html.toString());
    }
}
