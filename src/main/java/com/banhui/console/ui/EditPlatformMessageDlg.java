package com.banhui.console.ui;


import com.banhui.console.rpc.MessageProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditPlatformMessageDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditPlatformMessageDlg.class);

    private volatile long id;
    private volatile long auId;

    private Map<String, Object> row;

    public EditPlatformMessageDlg(
            long id
    ) {
        this.id = id;

        if (this.id == 0) {
            setTitle(controller().getMessage("createMsg"));
        } else {
            searchData();
            setTitle(getTitle() + id);
            controller().readOnly("loginName", true);
            controller().readOnly("title", true);
            controller().readOnly("brief", true);
            controller().readOnly("content", true);
            controller().readOnly("match", true);
            controller().hide("ok");
        }

        controller().connect("match", this::match);
    }

    private void match(Object o) {
        String key = controller().getText("loginName").trim();

        final Map<String, Object> params = new HashMap<>();
        params.put("key", key);

        new MessageProxy().matchAccUserInfoInvest(params)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::matchCallback, UPDATE_UI);
    }

    private void matchCallback(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            StringBuffer sb = new StringBuffer();
            for (Map<String, Object> map : maps) {
                sb.append(stringValue(map, "loginName")).append("(").append(stringValue(map, "realName")).append(")");
                auId = longValue(map, "auId");
                break;
            }
            controller().setText("loginName", sb.toString());
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();

            params.put("au-id", auId);
            params.put("title", controller().getText("title").trim());
            params.put("brief", controller().getText("brief").trim());
            params.put("content", controller().getText("content").trim());
            params.put("type", longValue("1"));

            new MessageProxy().createMsg(params)
                              .thenApplyAsync(Result::map)
                              .whenCompleteAsync(this::saveCallback, UPDATE_UI);

        } else {
            super.done(result);
        }
    }

    private void saveCallback(
            Map<String, Object> result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.row = result;
            super.done(OK);
        }
    }

    public final long getId() {
        return this.id;
    }

    private void searchData() {
        assertUIThread();

        new MessageProxy().queryMsgDetailByAmId(this.id)
                          .thenApplyAsync(Result::map)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void searchCallback(
            Map<String, Object> data,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().setText("loginName", stringValue(data, "loginName") + "(" + stringValue(data, "realName") + ")");
            controller().setText("title", stringValue(data, "title"));
            controller().setDecimal("brief", decimalValue(data, "brief"));
            controller().setText("content", stringValue(data, "content"));
        }
    }

    public Map<String, Object> getResultRow() {
        return this.row;
    }
}
