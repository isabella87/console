package com.banhui.console.ui;


import com.banhui.console.rpc.ProtocolSignProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.components.DialogPane;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;


public class CreditProtocolDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(CreditProtocolDlg.class);

    private volatile long pId;

    public CreditProtocolDlg(
            long pId
    ) {
        this.pId = pId;

        setTitle(getTitle() + pId);

        controller().connect("search", this::search);
        controller().connect("createAndUpload", this::createAndUpload);
        controller().connect("doSign", this::doSign);
        controller().connect("checkSign", this::checkSign);
        controller().connect("searchSignerInfo", this::searchSignerInfo);
        controller().connect("download", this::download);
        controller().connect("clean", this::clean);
    }

    private void clean(ActionEvent actionEvent) {

    }

    private void download(ActionEvent actionEvent) {

    }

    private void searchSignerInfo(ActionEvent actionEvent) {
        Map<String,Object> params = new HashMap<>();
        params.put("pId",pId);
        new ProtocolSignProxy().getCreditAccountInfoByPId(params)
                               .thenApplyAsync(Result::list)
                               .whenCompleteAsync(this::getCreditAccountInfoCallback,UPDATE_UI);
    }

    private void getCreditAccountInfoCallback(
            List<Map<String,Object>> maps,
            Throwable t
    ) {
        if(t!=null){
            ErrorHandler.handle(t);
        }else{

        }
    }

    private void checkSign(ActionEvent actionEvent) {
        Map<String,Object> params = new HashMap<>();
        params.put("pId",pId);
    }

    private void doSign(ActionEvent actionEvent) {
        Map<String,Object> params = new HashMap<>();
        params.put("pId",pId);
        new ProtocolSignProxy().doCreditAssignSign(params);
    }

    private void createAndUpload(ActionEvent actionEvent) {
        Map<String,Object> params = new HashMap<>();
        params.put("pId",pId);
        new ProtocolSignProxy().uploadCreditAssignFile(params);
    }

    private void search(ActionEvent actionEvent) {
        Map<String,Object> params = new HashMap<>();
        params.put("pId",pId);
        new ProtocolSignProxy().getForceCreditAgreementFilesInfoByPId(params)
                                .thenApplyAsync(Result::list)
                                .whenCompleteAsync(this::searchCallback,UPDATE_UI);
    }

    private void searchCallback(
            List<Map<String,Object>> maps,
            Throwable t
    ) {
        if(t!=null){
            ErrorHandler.handle(t);
        }else{

        }
    }

    public long getpId() {
        return pId;
    }
}
