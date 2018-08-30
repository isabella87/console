package com.banhui.console.ui;


import com.banhui.console.rpc.ProjectProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.swing.*;
import java.awt.*;
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
import static com.banhui.console.rpc.ResultUtils.dateValue;
import static com.banhui.console.rpc.ResultUtils.decimalValue;
import static com.banhui.console.rpc.ResultUtils.intValue;
import static com.banhui.console.rpc.ResultUtils.longValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;
import static org.xx.armory.swing.UIUtils.assertUIThread;

public class EditProjectsDlg
        extends DialogPane {
    /**
     * 查看/编辑项目
     * id 项目ID
     * nominalAuId 名义借款人id
     * financierId 借款人id
     * bondsmanAuId 代偿账户id
     * listName 表名
     * idName 表名中对应id名
     */
    private volatile long id;
    private Map<String, Object> row;
    private volatile Long nominalAuId;
    private volatile Long bondsmanAuId;
    private volatile Long financierId;
    private String listName;
    private String idName;

    public EditProjectsDlg(
            long id,
            long role
    ) {
        controller().readOnly("item-no", true);
        controller().readOnly("type", true);
        controller().readOnly("invest-max-amt-ratio", true);
        controller().readOnly("out-time", true);
        controller().readOnly("financier_cu", true);
        controller().readOnly("nominal_au", true);
        controller().readOnly("bondsman_au", true);
        controller().readOnly("interest", true);

        controller().setBoolean("for-per-invest-max-amt", true);
        controller().setBoolean("for-invest-max-amt", true);
        controller().setBoolean("for-in-time", true);

        controller().connect("guaranteePersons", "change", this::listChanged);
        controller().connect("guaranteeOrg", "change", this::listChanged2);
        controller().connect("borPersons", "change", this::listChanged3);
        controller().connect("borOrgs", "change", this::listChanged4);

        controller().connect("for-invest-max-amt-ratio", "change", this::checkboxInMaxAmtRatio);
        controller().connect("for-per-invest-max-amt", "change", this::checkboxPerMaxAmt);
        controller().connect("for-invest-max-amt", "change", this::checkboxInMaxAmt);
        controller().connect("for-in-time", "change", this::checkboxInOutTime);
        controller().connect("bonus-period", "change", this::changeBonusPeriod);
        controller().connect("preview", this::preview);
        controller().connect("chooseMgrPer", this::chooseMgrPer);
        controller().connect("chooseBondsMan", this::chooseBondsMan);
        controller().connect("cancelBondsMan", this::cancelBondsMan);
        controller().connect("operate", this::operate);
        controller().connect("editPermissible", this::editPermissible);
        controller().connect("delay", this::delay);

        controller().connect("createBondsman", this::createBondsman);
        controller().connect("deleteBondsman", this::deleteBondsman);
        controller().connect("createGuaOrg", this::createGuaOrg);
        controller().connect("deleteGuaOrg", this::deleteGuaOrg);

        controller().connect("createBorPer", this::createBorPer);
        controller().connect("deleteBorPer", this::deleteBorPer);
        controller().connect("createBorOrg", this::createBorOrg);
        controller().connect("deleteBorOrg", this::deleteBorOrg);

        controller().disable("delay");
        controller().disable("deleteBondsman");
        controller().disable("deleteGuaOrg");
        controller().disable("deleteBorPer");
        controller().disable("deleteBorOrg");

        controller().connect("amt", "change", this::interestChange);
        controller().connect("rate", "change", this::interestChange);
        controller().connect("borrow-days", "change", this::interestChange);
        controller().connect("in-time", "change", this::inTimeChange);

        this.id = id;
        if (this.id == 0) {
            setTitle(controller().getMessage("create") + getTitle() + id);
        } else {
            updateData();
            if (role == 0) {
                setTitle(controller().getMessage("view") + getTitle() + id);
                controller().readOnly(null, true);
                controller().hide("cancel");
                controller().hide("ok");
                controller().enable("close");
                controller().enable("preview");
                controller().enable("editPermissible");
            } else if (role == 1) {
                setTitle(controller().getMessage("edit") + getTitle() + id);
                controller().hide("close");
            }
        }
        JLabel perAmtLable = controller().get(JLabel.class, "per-amt");
        perAmtLable.setForeground(Color.red);
    }

    private void updateData() {
        assertUIThread();

        allDone(new ProjectProxy().queryPrjLoanById(this.id),
                new ProjectProxy().queryFinancierById(this.id),
                new ProjectProxy().prjRating(this.id),
                new ProjectProxy().queryPrjGuaranteePersons(id),
                new ProjectProxy().queryPrjGuaranteeOrg(id),
                new ProjectProxy().queryBorPersons(id),
                new ProjectProxy().queryBorOrgs(id)
        ).thenApply(results -> new Object[]{
                results[0].map(),
                results[1].list(),
                results[2].map(),
                results[3].list(),
                results[4].list(),
                results[5].list(),
                results[6].list()
        }).whenCompleteAsync(this::searchCallback, UPDATE_UI);

    }

    private void delay(
            ActionEvent actionEvent
    ) {
        final DelayFinancingDateDlg dlg = new DelayFinancingDateDlg(id);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Date datepoint = dlg.getDatepoint();
            if (datepoint != null) {
                controller().setDate("expected-borrow-time", datepoint);
            }
        }
    }

    private void createBondsman(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "guaranteePersons");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;

        final CreatePrjGuaranteeDlg dlg = new CreatePrjGuaranteeDlg(id, 1);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
    }

    private void deleteBondsman(
            ActionEvent actionEvent
    ) {
        listName = "guaranteePersons";
        idName = "pgp-id";
        final JTable table = controller().get(JTable.class, "guaranteePersons");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long pgpId = tableModel.getNumberByName(table.getSelectedRow(), "pgpId");

        String confirmDeleteText = controller().formatMessage("delete-guaPer", pgpId);
        if (confirm(null, confirmDeleteText)) {
            controller().disable("deleteBondsman");

            final Map<String, Object> params = new HashMap<>();
            params.put("p-id", id);
            params.put("pgp-id", pgpId);
            new ProjectProxy().deletePrjGuaranteePerson(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void createGuaOrg(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "guaranteeOrg");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;

        final CreatePrjGuaranteeDlg dlg = new CreatePrjGuaranteeDlg(id, 2);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
    }

    private void deleteGuaOrg(
            ActionEvent actionEvent
    ) {
        listName = "guaranteeOrg";
        idName = "pgo-id";
        final JTable table = controller().get(JTable.class, "guaranteeOrg");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long pgoId = tableModel.getNumberByName(table.getSelectedRow(), "pgoId");

        String confirmDeleteText = controller().formatMessage("delete-guaOrg", pgoId);
        if (confirm(null, confirmDeleteText)) {
            controller().disable("deleteGuaOrg");

            final Map<String, Object> params = new HashMap<>();
            params.put("p-id", id);
            params.put("pgo-id", pgoId);
            new ProjectProxy().deletePrjGuaranteeOrg(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);

        }
    }

    private void createBorPer(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "borPersons");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;
        List<Long> lists = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            lists.add(tableModel.getNumberByName(i, "bpmpId"));
        }
        final CreatePrjBorrowDlg dlg = new CreatePrjBorrowDlg(id, 3, lists);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
    }

    private void deleteBorPer(
            ActionEvent actionEvent
    ) {
        listName = "borPersons";
        idName = "bpmp-id";
        final JTable table = controller().get(JTable.class, listName);
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long bpmpId = tableModel.getNumberByName(table.getSelectedRow(), "bpmpId");

        String confirmDeleteText = controller().formatMessage("delete-borPer", bpmpId);
        if (confirm(null, confirmDeleteText)) {
            controller().disable("deleteBorPer");

            final Map<String, Object> params = new HashMap<>();
            params.put("p-id", id);
            params.put("bpmp-id", bpmpId);
            new ProjectProxy().deleteBorPerson(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void createBorOrg(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "borOrgs");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = 0;
        List<Long> lists = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            lists.add(tableModel.getNumberByName(i, "bpmoId"));
        }
        final CreatePrjBorrowDlg dlg = new CreatePrjBorrowDlg(id, 4, lists);
        dlg.setFixedSize(false);

        if (showModel(null, dlg) == DialogPane.OK) {
            Map<String, Object> row = dlg.getResultRow();
            if (row != null && !row.isEmpty()) {
                tableModel.insertRow(selectedRow, row);
            }
        }
    }

    private void deleteBorOrg(
            ActionEvent actionEvent
    ) {
        listName = "borOrgs";
        idName = "bpmo-id";
        final JTable table = controller().get(JTable.class, listName);
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final long bpmoId = tableModel.getNumberByName(table.getSelectedRow(), "bpmoId");

        String confirmDeleteText = controller().formatMessage("delete-borOrg", bpmoId);
        if (confirm(null, confirmDeleteText)) {
            controller().disable("deleteBorOrg");

            final Map<String, Object> params = new HashMap<>();
            params.put("p-id", id);
            params.put("bpmo-id", bpmoId);
            new ProjectProxy().deleteBorOrg(params)
                              .thenApplyAsync(Result::longValue)
                              .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }
    }

    private void delCallback(
            Long thisId,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            final JTable table = controller().get(JTable.class, listName);
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            tableModel.removeFirstRow(row -> Objects.equals(thisId.toString(), row.get(idName)));
        }
    }

    private void editPermissible(
            ActionEvent actionEvent
    ) {
        final EditPermissibleInvestorDlg dlg = new EditPermissibleInvestorDlg(id);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void chooseMgrPer(
            ActionEvent actionEvent
    ) {
        long allowRole = 2;
        final ChoosePrjAccountDlg dlg = new ChoosePrjAccountDlg(allowRole);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            if (dlg.getAuId() != 0) {
                controller().setText("financier_cu", userType(dlg.getUserType()) + dlg.getRealName());
                this.setFinancierId(dlg.getAuId());
            }
        }
    }

    private void chooseBondsMan(
            ActionEvent actionEvent
    ) {
        long allowRole = 3;
        final ChoosePrjAccountDlg dlg = new ChoosePrjAccountDlg(allowRole);
        dlg.setFixedSize(false);
        if (showModel(null, dlg) == DialogPane.OK) {
            if (dlg.getAuId() != 0) {
                controller().setText("bondsman_au", userType(dlg.getUserType()) + dlg.getRealName());
                this.setBondsmanAuId(dlg.getAuId());
            }
        }
    }

    private void cancelBondsMan(ActionEvent actionEvent) {
        controller().setText("bondsman_au", "");
        this.setBondsmanAuId(null);
    }


    private void preview(
            ActionEvent actionEvent
    ) {
        int bonusDay = controller().getInteger("bonus-day");
        int bonusPeriod = controller().getInteger("bonus-period");
        final BrowsePrjPreviewDlg dlg = new BrowsePrjPreviewDlg(id, bonusDay, bonusPeriod);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void operate(
            ActionEvent actionEvent
    ) {
        final ChooseProtocolDlg dlg = new ChooseProtocolDlg(id, 31,1);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }


    private void checkboxInMaxAmtRatio(
            Object event
    ) {
        if (controller().getBoolean("for-invest-max-amt-ratio")) {
            controller().readOnly("invest-max-amt-ratio", false);
        } else {
            controller().readOnly("invest-max-amt-ratio", true);
        }
    }


    @SuppressWarnings("unchecked")
    private void searchCallback(
            Object[] results,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            queryFinancier((List<Map<String, Object>>) results[1]);

            prjRating((Map<String, Object>) results[2]);

            JTable table1 = controller().get(JTable.class, "guaranteePersons");
            TypedTableModel tableModel1 = (TypedTableModel) table1.getModel();
            tableModel1.setAllRows((Collection<Map<String, Object>>) results[3]);

            JTable table2 = controller().get(JTable.class, "guaranteeOrg");
            TypedTableModel tableModel2 = (TypedTableModel) table2.getModel();
            tableModel2.setAllRows((Collection<Map<String, Object>>) results[4]);

            JTable table3 = controller().get(JTable.class, "borPersons");
            TypedTableModel tableModel3 = (TypedTableModel) table3.getModel();
            tableModel3.setAllRows((Collection<Map<String, Object>>) results[5]);

            JTable table4 = controller().get(JTable.class, "borOrgs");
            TypedTableModel tableModel4 = (TypedTableModel) table4.getModel();
            tableModel4.setAllRows((Collection<Map<String, Object>>) results[6]);

            updateDataCallback((Map<String, Object>) results[0]);
        }
    }

    private void prjRating(
            Map<String, Object> data
    ) {
        if (data != null && !data.isEmpty()) {
            controller().setText("prj-rating", stringValue(data, "prjRating"));
            controller().setNumber("s-all", longValue(data, "sAll"));
            controller().setText("prj-risk-assess", stringValue(data, "prjRiskAssess"));
        }
    }

    private void queryFinancier(
            List<Map<String, Object>> maps
    ) {
        if (maps != null && !maps.isEmpty()) {
            controller().setText("financier_cu", userType(stringValue(maps.get(0), "userType")) + stringValue(maps.get(0), "realName"));
            controller().setText("nominal_au", userType(stringValue(maps.get(1), "userType")) + stringValue(maps.get(1), "realName"));
            controller().setText("bondsman_au", userType(stringValue(maps.get(2), "userType")) + stringValue(maps.get(2), "realName"));
            this.setNominalAuId(longValue(maps.get(1), "auId"));
            this.setBondsmanAuId(longValue(maps.get(2), "auId"));
            this.setFinancierId(longValue(maps.get(0), "auId"));
        }
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            controller().disable("ok");

            final Map<String, Object> params = new HashMap<>();

            if (this.id != 0) {
                params.put("p-id", id);
            }
            if (this.financierId == null || this.financierId == 0) {
                String nullBorPer = controller().formatMessage("null-borPer");
                warn(null, nullBorPer);
                controller().enable("ok");
                return;
            }
            params.put("item-name", controller().getText("item-name").trim());
            params.put("item-show-name", controller().getText("item-show-name").trim());
            params.put("item-no", controller().getText("item-no").trim());
            params.put("amt", controller().getDecimal("amt"));
            params.put("rate", controller().getFloat("rate"));
            params.put("extension-rate", controller().getFloat("extension-rate"));
            params.put("time-out-rate", controller().getFloat("time-out-rate"));
            //params.put("penalty-ratio", controller().getText("penalty-ratio").trim());
            params.put("borrow-days", controller().getNumber("borrow-days"));
            params.put("extension-days", controller().getNumber("extension-days"));
            params.put("in-time", controller().getDate("in-time"));
            params.put("out-time", controller().getDate("out-time"));
            params.put("financing-days", controller().getNumber("financing-days"));
            params.put("expected-borrow-time", controller().getDate("expected-borrow-time"));
            params.put("per-invest-min-amt", controller().getDecimal("per-invest-min-amt"));
            params.put("per-invest-amt", controller().getDecimal("per-invest-amt"));
            params.put("core-guara-name", controller().getText("core-guara-name").trim());
            if (controller().getBoolean("for-invest-max-amt-ratio")) {
                params.put("invest-max-amt-ratio", controller().getDecimal("invest-max-amt-ratio"));
            } else {
                params.put("invest-max-amt-ratio", null);
            }
            if (controller().getBoolean("for-invest-max-amt")) {
                params.put("invest-max-amt", controller().getDecimal("invest-max-amt"));
            } else {
                params.put("invest-max-amt", params.get("amt"));
            }
            params.put("key", controller().getText("key").trim());
            params.put("water-mark", controller().getText("water-mark").trim());
            if (controller().getBoolean("for-per-invest-max-amt")) {
                params.put("per-invest-max-amt", controller().getDecimal("per-invest-max-amt"));
            } else {
                params.put("per-invest-max-amt", params.get("amt"));
            }
            params.put("fee-rate", controller().getFloat("fee-rate"));
            params.put("cost-fee", controller().getFloat("cost-fee"));
            params.put("out-proxy", controller().getText("out-proxy").trim());
            params.put("in-proxy", controller().getText("in-proxy").trim());
            params.put("type", controller().getNumber("type"));
            params.put("src", controller().getText("src").trim());
            params.put("remark", controller().getText("remark").trim());
            params.put("financier", controller().getText("financier").trim());
            params.put("deposit-ratio", controller().getFloat("deposit-ratio"));
            params.put("prj-intro", controller().getText("prj-intro").trim());
            params.put("loan-purposes", controller().getText("loan-purposes").trim());
            params.put("visible", true);
            params.put("sold-fee", 0);

            int flags = 0;
            if (controller().getBoolean("flag1")) {
                flags += 1;
            }
            if (controller().getBoolean("flag2")) {
                flags += 16;
            }
            if (controller().getBoolean("flag3")) {
                flags += 64;
            }
            params.put("flags", flags);
            int contract = 0;
            if (controller().getBoolean("contract1")) {
                contract += 1;
            }
            if (controller().getBoolean("contract2")) {
                contract += 2;
            }
            if (controller().getBoolean("contract3")) {
                contract += 8;
            }
            params.put("contract", contract);

            final Map<String, Object> paramsBonus = new HashMap<>();
            paramsBonus.put("p-id", this.id);
            int bonusPeriod = controller().getInteger("bonus-period");
            paramsBonus.put("bonus-period", bonusPeriod);
            if (bonusPeriod == 1) {
                paramsBonus.put("bonus-day", 1);
            } else {
                paramsBonus.put("bonus-day", controller().getInteger("bonus-day"));
            }
            paramsBonus.put("signature", null);

            final Map<String, Object> paramsRating = new HashMap<>();
            paramsRating.put("p-id", this.id);
            paramsRating.put("prj-rating", controller().getText("prj-rating"));
            paramsRating.put("s-all", controller().getNumber("s-all"));
            paramsRating.put("prj-risk-assess", controller().getText("prj-risk-assess"));

            final Map<String, Object> paramsFinancier = new HashMap<>();
            paramsFinancier.put("p-id", this.id);
            paramsFinancier.put("nominal-au-id", this.getNominalAuId());
            paramsFinancier.put("bondsman-au-id", this.getBondsmanAuId());
            paramsFinancier.put("signature", null);

            final Map<String, Object> paramsLoanFin = new HashMap<>();
            paramsLoanFin.put("p-id", this.id);
            paramsLoanFin.put("financier-cu-id", this.getFinancierId());
            paramsLoanFin.put("signature", null);

            allDone(new ProjectProxy().updatePrjLoan(params),
                    new ProjectProxy().updateBonusPeriod(paramsBonus),
                    new ProjectProxy().createPrjRating(paramsRating),
                    new ProjectProxy().updateFinancier(paramsFinancier),
                    new ProjectProxy().updateLoanFinancier(paramsLoanFin)
            ).thenApplyAsync(results -> new Object[]{
                    results[0].map(),
                    results[1].longValue(),
                    results[2].map(),
                    results[3].list(),
                    results[4].map()
            }, UPDATE_UI)
             .thenAcceptAsync(this::saveCallback, UPDATE_UI)
             .exceptionally(ErrorHandler::handle)
             .thenAcceptAsync(v -> controller().enable("ok"), UPDATE_UI);

        } else {
            super.done(result);
        }
    }

    @SuppressWarnings("unchecked")
    private void saveCallback(
            Object[] results
    ) {
        this.row = (Map<String, Object>) results[0];
        super.done(OK);
    }

    private void updateDataCallback(
            Map<String, Object> data
    ) {
        controller().setText("item-name", stringValue(data, "itemName"));
        controller().setText("item-show-name", stringValue(data, "itemShowName"));
        controller().setText("item-no", stringValue(data, "itemNo"));
        controller().setNumber("amt", longValue(data, "amt"));
        controller().setDecimal("rate", decimalValue(data, "rate"));
        controller().setDecimal("extension-rate", decimalValue(data, "extensionRate"));
        controller().setDecimal("time-out-rate", decimalValue(data, "timeOutRate"));
//        controller().setText("penalty-ratio", stringValue(data, "penaltyRatio"));
        controller().setNumber("borrow-days", longValue(data, "borrowDays"));
        controller().setNumber("extension-days", longValue(data, "extensionDays"));
        controller().setDate("in-time", dateValue(data, "inTime"));
        controller().setDate("out-time", dateValue(data, "outTime"));
        controller().setNumber("financing-days", longValue(data, "financingDays"));
        controller().setDate("expected-borrow-time", dateValue(data, "expectedBorrowTime"));
        controller().setDecimal("per-invest-min-amt", decimalValue(data, "perInvestMinAmt"));
        controller().setDecimal("per-invest-amt", decimalValue(data, "perInvestAmt"));
        if (data.get("investMaxAmtRatio") == null || data.get("investMaxAmtRatio").equals("null")) {
            controller().setBoolean("for-invest-max-amt-ratio", false);
        } else {
            controller().setBoolean("for-invest-max-amt-ratio", true);
            controller().setDecimal("invest-max-amt-ratio", decimalValue(data, "investMaxAmtRatio"));
        }
        controller().setDecimal("invest-max-amt", decimalValue(data, "investMaxAmt"));
        controller().setText("key", stringValue(data, "key"));
//        controller().setText("visible", stringValue(data, "visible"));
        controller().setText("water-mark", stringValue(data, "waterMark"));
        controller().setDecimal("per-invest-max-amt", decimalValue(data, "perInvestMaxAmt"));
        controller().setDecimal("fee-rate", decimalValue(data, "feeRate"));
        controller().setDecimal("cost-fee", decimalValue(data, "costFee"));
//        controller().setText("sold-fee", stringValue(data, "soldFee"));
        controller().setText("out-proxy", stringValue(data, "outProxy"));
        controller().setText("in-proxy", stringValue(data, "inProxy"));
        controller().setInteger("type", intValue(data, "type"));
        controller().setText("src", stringValue(data, "src"));
        controller().setText("remark", stringValue(data, "remark"));
        controller().setText("financier", stringValue(data, "financier"));
        controller().setDecimal("deposit-ratio", decimalValue(data, "depositRatio"));
        controller().setText("prj-intro", stringValue(data, "prjIntro"));
        controller().setText("loan-purposes", stringValue(data, "loanPurposes"));
        controller().setText("core-guara-name", stringValue(data, "coreGuaraName"));
        controller().setInteger("bonus-day", intValue(data, "bonusDay"));
        controller().setNumber("bonus-period", longValue(data, "bonusPeriod"));
//        controller().setText("financier_cu_id", stringValue(data, "financierCuId"));

        int contract = (int) data.get("contract");
        switch (contract) {
            case 1:
                controller().setBoolean("contract1", true);
                break;
            case 2:
                controller().setBoolean("contract2", true);
                break;
            case 8:
                controller().setBoolean("contract3", true);
                break;
            case 3:
                controller().setBoolean("contract1", true);
                controller().setBoolean("contract2", true);
                break;
            case 9:
                controller().setBoolean("contract1", true);
                controller().setBoolean("contract3", true);
                break;
            case 10:
                controller().setBoolean("contract2", true);
                controller().setBoolean("contract3", true);
                break;
            case 11:
                controller().setBoolean("contract1", true);
                controller().setBoolean("contract2", true);
                controller().setBoolean("contract3", true);
                break;
        }
        int flags = (int) data.get("flags");
        switch (flags) {
            case 1:
                controller().setBoolean("flag1", true);
                break;
            case 16:
                controller().setBoolean("flag2", true);
                break;
            case 64:
                controller().setBoolean("flag3", true);
                break;
            case 17:
                controller().setBoolean("flag1", true);
                controller().setBoolean("flag2", true);
                break;
            case 65:
                controller().setBoolean("flag1", true);
                controller().setBoolean("flag3", true);
                break;
            case 80:
                controller().setBoolean("flag2", true);
                controller().setBoolean("flag3", true);
                break;
            case 81:
                controller().setBoolean("flag1", true);
                controller().setBoolean("flag2", true);
                controller().setBoolean("flag3", true);
                break;

        }
        if (controller().getInteger("bonus-period") == 0) {
            controller().setNumber("bonus-period", 1L);
        }
        if (controller().getInteger("bonus-period") == 1) {
            controller().readOnly("bonus-day", true);
            controller().setNumber("bonus-day", 1L);
        }
        controller().setDecimal("interest", calInterest());
    }

    private void checkboxPerMaxAmt(
            Object event
    ) {
        if (controller().getBoolean("for-per-invest-max-amt")) {
            controller().readOnly("per-invest-max-amt", false);
        } else {
            controller().readOnly("per-invest-max-amt", true);
        }
    }

    private void checkboxInMaxAmt(
            Object event
    ) {
        if (controller().getBoolean("for-invest-max-amt")) {
            controller().readOnly("invest-max-amt", false);
        } else {
            controller().readOnly("invest-max-amt", true);
        }
    }

    private void checkboxInOutTime(
            Object event
    ) {
        if (controller().getBoolean("for-in-time")) {
            controller().readOnly("out-time", true);
        } else {
            controller().readOnly("out-time", false);
            controller().setDate("out-time", controller().getDate("in-time"));
        }
    }

    private void changeBonusPeriod(
            Object o
    ) {
        if (controller().getInteger("bonus-period") == 1) {
            controller().readOnly("bonus-day", true);
        } else {
            controller().readOnly("bonus-day", false);
        }
    }

    private String userType(String a) {
        switch (a) {
            case "1":
                return "(个人)";
            case "2":
                return "(机构)";
            default:
                return "";
        }
    }

    private void listChanged(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "guaranteePersons").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("deleteBondsman");
        } else if (selectedRows.length > 1) {
            controller().disable("deleteBondsman");
        } else {
            controller().disable("deleteBondsman");
        }
    }

    private void listChanged2(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "guaranteeOrg").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("deleteGuaOrg");
        } else if (selectedRows.length > 1) {
            controller().disable("deleteGuaOrg");
        } else {
            controller().disable("deleteGuaOrg");
        }
    }

    private void listChanged3(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "borPersons").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("deleteBorPer");
        } else if (selectedRows.length > 1) {
            controller().disable("deleteBorPer");
        } else {
            controller().disable("deleteBorPer");
        }
    }

    private void listChanged4(
            Object event
    ) {
        int[] selectedRows = controller().get(JTable.class, "borOrgs").getSelectedRows();
        if (selectedRows.length == 1) {
            controller().enable("deleteBorOrg");
        } else if (selectedRows.length > 1) {
            controller().disable("deleteBorOrg");
        } else {
            controller().disable("deleteBorOrg");
        }
    }

    private void interestChange(
            Object o
    ) {
        controller().setDecimal("interest", calInterest());
    }

    private void inTimeChange(
            Object o
    ) {
        if (controller().getBoolean("for-in-time")) {
            controller().setDate("out-time", controller().getDate("in-time"));
        }
    }

    private BigDecimal calInterest(
    ) {
        BigDecimal amt = controller().getDecimal("amt");
        double rate = controller().getFloat("rate");
        long day = controller().getNumber("borrow-days");
        double num = amt.doubleValue() * rate / 100 * day / 365;
        final BigDecimal interest = new BigDecimal(num).setScale(2, BigDecimal.ROUND_HALF_UP);
        return interest;
    }

    public Map<String, Object> getResultRow() {
        return this.row;
    }

    public Long getNominalAuId() {
        return nominalAuId;
    }

    public void setNominalAuId(Long nominalAuId) {
        this.nominalAuId = nominalAuId;
    }

    public Long getBondsmanAuId() {
        return bondsmanAuId;
    }

    public void setBondsmanAuId(Long bondsmanAuId) {
        this.bondsmanAuId = bondsmanAuId;
    }

    public Long getFinancierId() {
        return financierId;
    }

    public void setFinancierId(Long financierId) {
        this.financierId = financierId;
    }
}
