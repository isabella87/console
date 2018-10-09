package com.banhui.console.ui;

import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowsePrjSignatureDlg
        extends DialogPane {
    private volatile long pid;

    public BrowsePrjSignatureDlg(
            long pid
    ) {
        this.pid = pid;
        if (this.pid != 0) {
            setTitle(getTitle() + pid);
        }
        controller().connect("search", this::search);
        controller().connect("clear-plat", this::clearPlat);
        controller().connect("clear-multi", this::clearMulti);
        controller().connect("create-upload", this::createUpload);
        controller().connect("download", this::download);
        controller().connect("sign-plat", this::signPlat);
        controller().connect("sign-multi", this::signMulti);
        controller().connect("check-sign", this::checkSign);
        controller().connect("check-info", this::checkInfo);
        controller().connect("synchronize", this::synchronize);

        controller().call("search");
    }

    private void synchronize(
            ActionEvent actionEvent
    ) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

            @Override
            public String getTitle() {
                return controller().getMessage("synchronize-text");
            }

            @Override
            protected Collection<Map<String, Object>> load() {
                final JTable table = controller().get(JTable.class, "list");
                final TypedTableModel tableModel = (TypedTableModel) table.getModel();
                List<Map<String, Object>> retList = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Map<String, Object> params = new HashMap<>();
                    String fileName = tableModel.getStringByName(i, "fileName");
                    params.put("p-id", pid);
                    params.put("file-name", fileName);
                    retList.add(params);
                }
                return retList;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在同步合同：" + params.get("file-name");
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            ) {
                new ProjectProxy().sync(params)
                                  .thenApplyAsync(Result::map)
                                  .whenCompleteAsync(this::saveCallback, UPDATE_UI).join();
            }

            private void saveCallback(
                    Map<String, Object> map,
                    Throwable t
            ) {
            }

        });
        showModel(null, dlg);
        controller().call("search");
    }

    private void checkSign(
            ActionEvent actionEvent
    ) {
        controller().readOnly(null, true);
        new ProjectProxy().checkSign(pid)
                          .thenApplyAsync(Result::booleanValue)
                          .whenCompleteAsync(this::checkCallback, UPDATE_UI);
    }

    private void checkCallback(
            Boolean flag,
            Throwable t
    ) {
        if (t != null) {
            controller().call("search");
            ErrorHandler.handle(t);
        } else {
            controller().call("search");
        }
    }

    private void checkInfo(
            ActionEvent actionEvent
    ) {
        final BrowseBorSignInfoDlg dlg = new BrowseBorSignInfoDlg(pid);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void signPlat(
            ActionEvent actionEvent
    ) {
        controller().readOnly(null, true);
        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", pid);
        params.put("category", 1);
        new ProjectProxy().sign(params)
                          .thenApplyAsync(Result::array)
                          .whenCompleteAsync(this::completeCallback, UPDATE_UI);
    }

    private void signMulti(
            ActionEvent actionEvent
    ) {
        controller().readOnly(null, true);
        final Map<String, Object> params = new HashMap<>();
        params.put("p-id", pid);
        params.put("category", 2);
        new ProjectProxy().sign(params)
                          .thenApplyAsync(Result::array)
                          .whenCompleteAsync(this::completeCallback, UPDATE_UI);
    }


    private void download(
            ActionEvent actionEvent
    ) {
        final ChooseProtocolDlg dlg = new ChooseProtocolDlg(pid, 40, 2);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void createUpload(
            ActionEvent actionEvent
    ) {
        final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {

            @Override
            public String getTitle() {
                return controller().getMessage("upload-text");
            }

            @Override
            protected Collection<Map<String, Object>> load() {

                List<Map<String, Object>> retList = new ArrayList<>();
                for (int i = 1; i <= 6; i++) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("p-id", pid);
                    params.put("code-no", i);
                    retList.add(params);
                }
                return retList;
            }

            @Override
            protected String getCurrent(
                    int i,
                    Map<String, Object> params
            ) {
                return "正在执行合同：" + controller().getMessage("file" + (i + 1));
            }

            @Override
            protected void execute(
                    int i,
                    Map<String, Object> params
            ) {
                new ProjectProxy().createAndUpload(params)
                                  .thenApplyAsync(Result::stringValue)
                                  .whenCompleteAsync(this::saveCallback, UPDATE_UI).join();
            }

            private void saveCallback(
                    String str,
                    Throwable t
            ) {
            }

        });
        showModel(null, dlg);
        controller().call("search");
    }

    private void clearPlat(
            ActionEvent actionEvent
    ) {
        String clearConfirmText = controller().formatMessage("clear-confirm-text");
        String confirmText = controller().formatMessage("confirm-text");
        if (confirm(null, clearConfirmText, confirmText)) {
            final Map<String, Object> params = new HashMap<>();
            params.put("p-id", pid);
            params.put("category", 1);
            new ProjectProxy().abandon(params)
                              .thenApplyAsync(Result::array)
                              .whenCompleteAsync(this::completeCallback, UPDATE_UI);
        }
    }

    private void clearMulti(
            ActionEvent actionEvent
    ) {
        String clearConfirmText = controller().formatMessage("clear-confirm-text");
        String confirmText = controller().formatMessage("confirm-text");
        if (confirm(null, clearConfirmText, confirmText)) {
            final Map<String, Object> params = new HashMap<>();
            params.put("p-id", pid);
            params.put("category", 2);
            new ProjectProxy().abandon(params)
                              .thenApplyAsync(Result::array)
                              .whenCompleteAsync(this::completeCallback, UPDATE_UI);
        }
    }

    private void completeCallback(
            Object[] objects,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().call("search");
        }
    }

    private void search(
            ActionEvent actionEvent
    ) {
        controller().disable("search");
        new ProjectProxy().signatureList(pid)
                          .thenApplyAsync(Result::list)
                          .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, "list");
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.setAllRows(maps);
            UpdateUI();
        }
    }

    private void UpdateUI() {
        controller().enable("search");
        controller().disable("create-upload");
        controller().disable("upload");
        controller().disable("sign-plat");
        controller().disable("sign-multi");
        controller().disable("check-sign");
        controller().disable("synchronize");
        controller().disable("check-info");
        controller().disable("download");
        controller().disable("clear-plat");
        controller().disable("clear-multi");
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int row = tableModel.getRowCount();
        List<Integer> categorys = new ArrayList<>();
        List<Integer> signs = new ArrayList<>();
        for (int i = 0; i < table.getRowCount(); i++) {
            int category = tableModel.getNumberByName(i, "category").intValue();
            int sign = tableModel.getNumberByName(i, "signStatus").intValue();
            if (category == 1 && sign == -1) {
                controller().enable("sign-plat");
            }
            if (category == 2 && sign == -1) {
                controller().enable("sign-multi");
            }
            if (category == 2 && sign > -1) {
                controller().enable("check-info");
            }
            categorys.add(category);
            signs.add(sign);
        }
        if (categorys.contains(1)) {
            controller().enable("clear-plat");
        }
        if (categorys.contains(2)) {
            controller().enable("clear-multi");
        }
        if (signs.contains(0) || signs.contains(1) || signs.contains(2)) {
            controller().disable("create-upload");
            controller().enable("check-sign");
        } else {
            controller().enable("create-upload");
        }
        if (signs.size() > 0) {
            controller().enable("synchronize");
        }
        if (row > 0) {
            controller().enable("download");
        }
    }

}

