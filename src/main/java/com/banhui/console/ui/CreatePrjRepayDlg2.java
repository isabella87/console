package com.banhui.console.ui;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;

public class CreatePrjRepayDlg2
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(CreatePrjRepayDlg2.class);

    private volatile boolean flag;


    private static Map<Integer, String> paytypeMap = new HashMap<>();

    static {
        paytypeMap.put(1, "正常");
        paytypeMap.put(2, "保证金");
        paytypeMap.put(3, "名义借款人");
        paytypeMap.put(99, "其他");
    }

    public CreatePrjRepayDlg2(
            Map<String, Object> data
    ) {
        initData(data);

        controller().readOnly("remark", true);
        controller().readOnly("amt", true);
        controller().readOnly("is-on-time-repay", true);
        controller().readOnly("real-paid-time", true);
        controller().readOnly("pay-type", true);

        /*controller().readOnly("unpaid-amt", true);
        controller().readOnly("sub-amt", true);
        controller().readOnly("pay-time", true);*/
    }

    private void initData(Map<String, Object> data) {
        /*controller().setDecimal("unpaid-amt", decimalValue(data, "unpaid-amt"));
        controller().setDecimal("sub-amt", decimalValue(data, "sub-amt"));
        controller().setDate("pay-time", dateValue(data, "pay-time"));*/

        controller().setDate("real-paid-time", dateValue(data, "paid-time"));//
        controller().setDecimal("amt", decimalValue(data, "amt"));//
        controller().setText("remark", stringValue(data, "remark"));//
        controller().setInteger("pay-type", intValue(data, "pay-type"));//
        controller().setBoolean("is-on-time-repay", booleanValue(data, "is-on-time-repay"));
    }

    @Override
    public void done(int result) {
        if (result == OK) {

            flag = true;
            super.done(OK);

        } else {
            super.done(CANCEL);
        }
    }


}
