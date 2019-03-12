package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CreateProjectFrame
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(CreateProjectFrame.class);
    private Map<String, Object> row;
    private long pId;

    public CreateProjectFrame(
    ) {
        setTitle(getTitle());
    }

    public final long getId() {
        return this.pId;
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");
            final Map<String, Object> params = new HashMap<>();

            params.put("item-name", controller().getText("item-name").trim());
            params.put("type", controller().getNumber("type"));
            params.put("amt", 1000);
            params.put("borrow-days", 180);
            params.put("per-invest-min-amt", 1000);
            params.put("per-invest-amt", 1000);
            params.put("fee-rate", 6);
            params.put("per-invest-max-amt", 1000);
            params.put("invest-max-amt", 0);
            params.put("in-time", new Date());
            params.put("out-time", new Date());
            params.put("expected-borrow-time", new Date());
            params.put("flags", 0);
            params.put("contract", 0);
            params.put("visible", true);
            params.put("cost-fee", 0);
            params.put("sold-fee", 0);
            params.put("invest-max-amt-ratio", 0);
            params.put("deposit-ratio", 0);

            new ProjectProxy().createPrjLoan(params)
                              .thenApplyAsync(Result::map, UPDATE_UI)
                              .whenCompleteAsync(this::saveCallback);
        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Map<String, Object> row,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            Integer pId = (Integer) row.get("pId");
            this.pId = pId;
            super.done(OK);
        }
    }


    public Map<String, Object> getResultRow() {
        return this.row;
    }
}
