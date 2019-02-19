package com.banhui.console.ui;


import com.banhui.console.rpc.ProjectRepayProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.Application;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.ProgressDialog;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.banhui.console.rpc.ResultUtils.allDone;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class EditPrjRepayDlg
        extends DialogPane {
    private final Logger logger = LoggerFactory.getLogger(EditPrjRepayDlg.class);

    private volatile long pId;
    private volatile long status;

    private static Map<String, String> tranTypes = new HashMap<>();

    static {
        tranTypes.put("0", "利息");
        tranTypes.put("1", "本金");
        tranTypes.put("2", "罚息");
    }

    public EditPrjRepayDlg(
            long pId,
            long status
    ) {
        this.pId = pId;
        this.status = status;

        setTitle(getTitle() + pId);

        controller().connect("create", this::create);
        controller().connect("forbid", this::forbid);
        controller().connect("refresh", this::refresh);
        controller().connect("search", this::search);
        controller().connect("export", this::export);
        controller().connect("create-bonus", this::createBonus);
        controller().connect("edit-bonus", this::editBonus);
        controller().connect("delete-bonus", this::deleteBonus);
        controller().connect("list", "change", this::listChange);
        controller().connect("detail", "change", this::detailListChange);

        controller().call("refresh");

        controller().disable("create");
        controller().disable("forbid");
        controller().disable("search");

    }

    private void deleteBonus(ActionEvent actionEvent) {
        String confirmDeleteText = controller().formatMessage("confirm-delete-text");
        if (confirm(null, confirmDeleteText)) {


            final ProgressDialog dlg = new ProgressDialog(new ProgressDialog.ProgressRunner<Map<String, Object>>() {
                @Override
                public String getTitle() {
                    return controller().getMessage("delete-bonus-execution");
                }

                @Override
                protected Collection<Map<String, Object>> load() {
                    final JTable table = controller().get(JTable.class, "list");
                    final TypedTableModel tableModel = (TypedTableModel) table.getModel();
                    final int[] selectedRows = table.getSelectedRows();
                    List<Map<String, Object>> retList = new ArrayList<>();
                    for (int selectedRow : selectedRows) {
                        int modelSelectRow = table.convertRowIndexToModel(selectedRow);
                        Map<String, Object> params = new HashMap<>();
                        params.put("pId", pId);
                        params.put("tran-no", tableModel.getNumberByName(modelSelectRow, "tranNo"));
                        params.put("tran-type", tableModel.getNumberByName(modelSelectRow, "tranType"));
                        retList.add(params);
                    }

                    return retList;
                }

                @Override
                protected String getCurrent(
                        int i,
                        Map<String, Object> params
                ) {
                    return "正在执行:(期数- " + params.get("tran-no") + ",类型-" + tranTypes.get(params.get("tran-type").toString());
                }

                @Override
                protected void execute(
                        int i,
                        Map<String, Object> params
                ) {
                    new ProjectRepayProxy().deletePrjBonus(params)
                                           .thenApplyAsync(Result::map)
                                           .whenCompleteAsync(this::deleteCallback, UPDATE_UI);
                }

                private void deleteCallback(
                        Map<String, Object> deletedRow,
                        Throwable t
                ) {
                    if (t != null) {
                        ErrorHandler.handle(t);
                    } else {
                        JTable table = controller().get(JTable.class, "list");
                        TypedTableModel tableModel = (TypedTableModel) table.getModel();
                        tableModel.removeFirstRow(row -> (Objects.equals(deletedRow.get("tranType"), row.get("tranType")) &&
                                Objects.equals(deletedRow.get("tranNo"), row.get("tranNo")))
                        );
                    }
                }
            });

            showModel(null, dlg);
        }
    }

    private void editBonus(ActionEvent actionEvent) {
        controller().disable("edit-bonus");
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());

        long tranType = tableModel.getNumberByName(selectedRow1, "tranType");
        long tranNo = tableModel.getNumberByName(selectedRow1, "tranNo");
        Date dueTime = tableModel.getDateByName(selectedRow1, "dueTime");
        BigDecimal amt = tableModel.getBigDecimalByName(selectedRow1, "amt");
        CreatePrjBonusDlg dlg = new CreatePrjBonusDlg(pId, amt, dueTime, tranType, tranNo);
        dlg.setFixedSize(false);

        if (showModel(Application.mainFrame(), dlg) == DialogPane.OK) {
            Map<String, Object> results = dlg.getResultRow();
            if (results != null && !results.isEmpty()) {
            }
            tableModel.setRow(selectedRow1, results);
        }
        controller().enable("edit-bonus");
    }

    private void createBonus(ActionEvent actionEvent) {
        controller().disable("create-bonus");
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        int selectedRow = 0;

        CreatePrjBonusDlg dlg = new CreatePrjBonusDlg(pId, null, null, 0, 0);
        dlg.setFixedSize(false);

        if (showModel(Application.mainFrame(), dlg) == DialogPane.OK) {
            Map<String, Object> results = dlg.getResultRow();
            if (results != null && !results.isEmpty()) {
            }
            tableModel.insertRow(selectedRow, results);
        }
        controller().enable("create-bonus");
    }

    private void export(ActionEvent actionEvent) {
        controller().disable("export");

        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        new ExcelExportUtil(getTitle(), tableModel).choiceDirToSave(getTitle());
        controller().enable("export");
    }

    private void forbid(ActionEvent actionEvent) {
        JTable table = controller().get(JTable.class, "detail");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        final int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 1) {
            int modelSelectRow = table.convertRowIndexToModel(selectedRows[0]);
            final long pbdId = tableModel.getNumberByName(modelSelectRow, "pbdId");
            if (confirm(this.getOwner(), controller().formatMessage("confirm-delete", pbdId))) {
                Map<String, Object> params = new HashMap<>();
                params.put("pId", pId);
                params.put("pbdId", pbdId);
                new ProjectRepayProxy().delPrjBonusDetail(params)
                                       .thenApplyAsync(Result::longValue)
                                       .whenCompleteAsync(this::forbidCallback, UPDATE_UI);
            }

        }


    }

    private void forbidCallback(
            Long optionalLong,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            new ProjectRepayProxy().queryPrjBonusDetail(pId)
                                   .thenApplyAsync(Result::list)
                                   .whenCompleteAsync(this::queryBonusDetailCallback, UPDATE_UI);
        }
    }

    private void queryBonusDetailCallback(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            JTable table = controller().get(JTable.class, "detail");
            TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.setAllRows(maps);
        }
    }

    private void detailListChange(Object actionEvent) {
        JTable table = controller().get(JTable.class, "detail");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();

        final int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 1) {
            controller().enable("search");
            int modelSelectRow = table.convertRowIndexToModel(selectedRows[0]);
            final long uploadStatus = tableModel.getNumberByName(modelSelectRow, "uploadStatus");
            if (uploadStatus != -1) {
                controller().enable("forbid");
            } else {
                controller().disable("forbid");
            }

        } else {
            controller().disable("search");
            controller().disable("forbid");
        }
    }

    private void search(ActionEvent actionEvent) {

        JTable table = controller().get(JTable.class, "detail");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());
        long pbdId = tableModel.getNumberByName(selectedRow1, "pbdId");
        long tranType = tableModel.getNumberByName(selectedRow1, "tranType");
        final long uploadStatus = tableModel.getNumberByName(selectedRow1, "uploadStatus");

        EditBonusDetailRepaysDlg dlg = new EditBonusDetailRepaysDlg(pbdId, pbdId, tranType, uploadStatus);
        dlg.setFixedSize(false);

        showModel(null, dlg);
    }

    private void listChange(Object actionEvent) {
        JTable table = controller().get(JTable.class, "list");

        final int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 1) {
            if (status == 90) {
                controller().enable("create");
                controller().enable("create-bonus");
                controller().enable("edit-bonus");
            } else {
                controller().disable("create");
                controller().disable("create-bonus");
                controller().disable("edit-bonus");
                controller().disable("delete-bonus");
            }

        } else {
            controller().disable("create");
            controller().disable("edit-bonus");
        }

    }

    private void refresh(
            ActionEvent event
    ) {
        allDone(new ProjectRepayProxy().queryPrjBonus(pId),
                new ProjectRepayProxy().queryPrjBonusDetail(pId)
        ).thenApply(results -> new Object[]{
                results[0].list(),
                results[1].list()
        }).whenCompleteAsync(this::refreshCallback, UPDATE_UI);
    }

    @SuppressWarnings("unchecked")
    private void refreshCallback(
            Object[] results,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            JTable table1 = controller().get(JTable.class, "list");
            TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
            tableModel1.setAllRows((Collection<Map<String, Object>>) results[0]);

            JTable table2 = controller().get(JTable.class, "detail");
            TypedTableModel tableModel2 = (TypedTableModel) table2.getModel();
            tableModel2.setAllRows((Collection<Map<String, Object>>) results[1]);
        }

    }

    private void create(
            ActionEvent actionEvent
    ) {
        JTable table = controller().get(JTable.class, "list");
        TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow1 = table.convertRowIndexToModel(table.getSelectedRow());

        final BigDecimal unpaidAmt = tableModel.getBigDecimalByName(selectedRow1, "unpaidAmt");
        final Date dueTime = tableModel.getDateByName(selectedRow1, "dueTime");
        final long tranType = tableModel.getNumberByName(selectedRow1, "tranType");
        final long tranNo = tableModel.getNumberByName(selectedRow1, "tranNo");

        CreatePrjRepayDlg dlg = new CreatePrjRepayDlg(pId, unpaidAmt, dueTime, tranType, tranNo);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                JTable detailTable = controller().get(JTable.class, "detail");
                TypedTableModel detailTableModel = (TypedTableModel) detailTable.getModel();
                detailTableModel.insertRow(0, dlg.getResultRow());
            }
        }
    }

    public final long getId() {
        return this.pId;
    }

}
