package com.banhui.console.ui;

import com.banhui.console.rpc.AccountsProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.dateValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseBankOrgInfoDlg
        extends DialogPane {
    private volatile long auId;

    public BrowseBankOrgInfoDlg(
            long auId
    ) {
        this.auId = auId;
        if (this.auId != 0) {
            setTitle(getTitle() + auId);
            new AccountsProxy().bankOrgInfo(auId)
                               .thenApplyAsync(Result::map)
                               .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                               .exceptionally(ErrorHandler::handle);
            controller().connect("list", "change", this::listChanged);
        }
    }

    private void listChanged(
            Object event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        int[] selectedRows = table.getSelectedRows();
        final JTextPane textPane = controller().get(JTextPane.class, "content");
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        if (selectedRows.length == 1) {
            // 选中了行，并且仅选中一行。
            int selectRow = table.getSelectedRow();
            textPane.setText(controller().getMessage("row" + selectRow));
        } else {
            textPane.setText(null);
        }
    }

    private void searchCallback(
            Map<String, Object> map
    ) {
        List<Map<String, Object>> result = new ArrayList<>();

        final Map<String, Object> params1 = new HashMap<>();
        params1.put("name", "机构名称");
        params1.put("value", map.get("name"));
        result.add(params1);

        final Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "证件类型");
        params2.put("value", map.get("raceCode"));
        result.add(params2);

        final Map<String, Object> params3 = new HashMap<>();
        params3.put("name", "证件号码");
        params3.put("value", map.get("idCard"));
        result.add(params3);

        final Map<String, Object> params4 = new HashMap<>();
        params4.put("name", "联系电话");
        params4.put("value", map.get("mobile"));
        result.add(params4);

        final Map<String, Object> params5 = new HashMap<>();
        params5.put("name", "对公账号");
        params5.put("value", map.get("reCard"));
        result.add(params5);

        final Map<String, Object> params6 = new HashMap<>();
        params6.put("name", "营业执照号");
        params6.put("value", map.get("busId"));
        result.add(params6);

        final Map<String, Object> params7 = new HashMap<>();
        params7.put("name", "税务登记号");
        params7.put("value", map.get("taxId"));
        result.add(params7);

        final Map<String, Object> params8 = new HashMap<>();
        params8.put("name", "电子账户号");
        params8.put("value", map.get("userId"));
        result.add(params8);

        final Map<String, Object> params9 = new HashMap<>();
        params9.put("name", "开户日期");
        Date date = dateValue(map, "datePoint");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (date != null) {
            params9.put("value", sdf.format(date));
        }
        result.add(params9);

        final Map<String, Object> params10 = new HashMap<>();
        params10.put("name", "是否已设置密码");
        Boolean flag = booleanValue(map, "isPwdSet");
        if (flag != null && flag) {
            params10.put("value", "是");
        } else {
            params10.put("value", "否");
        }
        result.add(params10);

        final Map<String, Object> params11 = new HashMap<>();
        params11.put("name", "角色");
        if (map.get("identity") != null) {
            int role = (int) map.get("identity");
            switch (role) {
                case 1:
                    params11.put("value", "投资人");
                    break;
                case 2:
                    params11.put("value", "借款人");
                    break;
                case 3:
                    params11.put("value", "担保人");
                    break;
            }
        } else {
            params11.put("value", "未知");
        }
        result.add(params11);

        final Map<String, Object> params12 = new HashMap<>();
        params12.put("name", "总余额");
        if (map.get("visibleBal") != null) {
            params12.put("value", map.get("visibleBal"));
        } else {
            params12.put("value", 0);
        }
        result.add(params12);

        final Map<String, Object> params13 = new HashMap<>();
        params13.put("name", "可用余额");
        if (map.get("availableBal") != null) {
            params13.put("value", map.get("availableBal"));
        } else {
            params13.put("value", 0);
        }
        result.add(params13);

        final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
        tableModel.setAllRows(result);
    }
}
