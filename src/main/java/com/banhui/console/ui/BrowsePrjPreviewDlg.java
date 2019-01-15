package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowsePrjPreviewDlg extends DialogPane {

    public BrowsePrjPreviewDlg(
            long id,
            int bonusDay,
            int bonusPeriod
    ) {
        final Map<String, Object> params = new HashMap<>();
        if (id != 0) {
            params.put("p-id", id);
        }
        if (bonusDay != 0) {
            params.put("bonus-day", bonusDay);
        }
        if (bonusPeriod != 0) {
            params.put("bonus-period", bonusPeriod);
        }
        new ProjectProxy().previewLoanPrjBonus(params)
                          .thenApplyAsync(Result::list)
                          .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                          .exceptionally(ErrorHandler::handle);
    }

    private void searchCallback(
            List<Map<String, Object>> c
    ) {
        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        final Map<String, Object> params = new HashMap<>();
        BigDecimal total = new BigDecimal(0.00).setScale(2, RoundingMode.HALF_UP);
        for (final Map<String, Object> map : c) {
            final BigDecimal amt = decimalValue(map, "amt").setScale(2, RoundingMode.HALF_UP);
            if (amt != null) {
                total = total.add(amt);
            }
        }
        params.put("type", 2);
        params.put("amt", total);
        c.add(params);
        tableModel.setAllRows(c);
    }
}
