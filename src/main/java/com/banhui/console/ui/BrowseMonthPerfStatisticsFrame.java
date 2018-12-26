package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.InternalFramePane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.ui.InputUtils.yesterday;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.warn;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseMonthPerfStatisticsFrame
        extends InternalFramePane {
    private List<DefaultMutableTreeNode> selectTreeNodes;

    public BrowseMonthPerfStatisticsFrame() {
        controller().disable("name-list");
        controller().setDate("datepoint", yesterday(new Date()).getStart());

        controller().connect("refresh", this::refreshUser);
        controller().connect("list", "change", this::listChanged);
        controller().connect("choose-depart", this::chooseDepart);
        controller().connect("statistics", this::doStatistics);
        controller().connect("name-list", this::userMonth);
        controller().call("refresh");

        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        MainFrame.setTableTitleAndTableModel(getTitle(),tableModel);
    }

    private void userMonth(
            ActionEvent actionEvent
    ) {
        final JTable table = controller().get(JTable.class, "list");
        final TypedTableModel tableModel = (TypedTableModel) table.getModel();
        final int selectedRow = table.getSelectedRow();
        final String uName = tableModel.getStringByName(selectedRow, "uName");
        final Date datepoint = controller().getDate("datepoint");

        CrmMonthStatisticsDlg dlg = new CrmMonthStatisticsDlg(uName, datepoint);
        dlg.setFixedSize(false);
        showModel(null, dlg);
    }

    private void doStatistics(
            ActionEvent actionEvent
    ) {
        controller().disable("statistics");
        JTree mgrJTree = controller().get(JTree.class, "mgrJTree");
        TreePath[] treePaths = mgrJTree.getSelectionPaths();
        StringBuffer sb = new StringBuffer();
        if (treePaths != null) {
            for (int i = 0; i < treePaths.length; i++) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePaths[i].getLastPathComponent();
                if (treeNode != null && !treeNode.getUserObject().toString().isEmpty()) {
                    String uName = treeNode.getUserObject().toString();
                    if (uName.contains("(")) {
                        uName = uName.substring(0, uName.lastIndexOf("(")) + ",";
                    } else if (uName.equals("我自己")) {
                        uName = "+,";
                    }
                    sb.append(uName);
                }
            }
        }
        final Map<String, Object> params = new HashMap<>();
        params.put("datepoint", controller().getDate("datepoint"));
        params.put("u-names", sb.toString());
        new CrmProxy().queryUserMonth(params)
                      .thenApplyAsync(Result::list)
                      .whenCompleteAsync(this::searchCallback, UPDATE_UI);
    }

    private void searchCallback(
            Collection<Map<String, Object>> c,
            Throwable t
    ) {
        if (t != null) {
            warn(null, t.getCause().getMessage());
        } else {
            final TypedTableModel tableModel = (TypedTableModel) controller().get(JTable.class, "list").getModel();
            tableModel.setAllRows(c);

            BigDecimal total1 = new BigDecimal(0);
            BigDecimal total2 = new BigDecimal(0);
            BigDecimal total3 = new BigDecimal(0);
            Long total4 = 0L;
            Long total5 = 0L;
            Long total6 = 0L;
            BigDecimal total7 = new BigDecimal(0);
            BigDecimal total8 = new BigDecimal(0);
            BigDecimal total9 = new BigDecimal(0);

            final Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                BigDecimal amt1 = tableModel.getBigDecimalByName(i, "sumInvestAmt");
                if (amt1 == null) {
                    amt1 = new BigDecimal(0);
                }
                total1 = total1.add(amt1);
                BigDecimal amt2 = tableModel.getBigDecimalByName(i, "sumTenderAmt");
                if (amt2 == null) {
                    amt2 = new BigDecimal(0);
                }
                total2 = total2.add(amt2);
                BigDecimal amt3 = tableModel.getBigDecimalByName(i, "sumRepaidCapitalAmt");
                if (amt3 == null) {
                    amt3 = new BigDecimal(0);
                }
                total3 = total3.add(amt3);
                Long amt4 = tableModel.getNumberByName(i, "sumFirstInvestCount");
                if (amt4 == null) {
                    amt4 = 0L;
                }
                total4 = total4 + amt4;
                Long amt5 = tableModel.getNumberByName(i, "sumInvesterCount");
                if (amt5 == null) {
                    amt5 = 0L;
                }
                total5 = total5 + amt5;
                Long amt6 = tableModel.getNumberByName(i, "sumInvestCount");
                if (amt6 == null) {
                    amt6 = 0L;
                }
                total6 = total6 + amt6;
                BigDecimal amt7 = tableModel.getBigDecimalByName(i, "sumCreditAmt");
                if (amt7 == null) {
                    amt7 = new BigDecimal(0);
                }
                total7 = total7.add(amt7);
                BigDecimal amt8 = tableModel.getBigDecimalByName(i, "sumBindCount");
                if (amt8 == null) {
                    amt8 = new BigDecimal(0);
                }
                total8 = total8.add(amt8);
                BigDecimal amt9 = tableModel.getBigDecimalByName(i, "sumIncomeAmt");
                if (amt9 == null) {
                    amt9 = new BigDecimal(0);
                }
                total9 = total9.add(amt9);
            }
            params.put("uName", "<总计>");
            params.put("sumInvestAmt", total1);
            params.put("sumTenderAmt", total2);
            params.put("sumRepaidCapitalAmt", total3);
            params.put("sumFirstInvestCount", total4);
            params.put("sumInvesterCount", total5);
            params.put("sumInvestCount", total6);
            params.put("sumCreditAmt", total7);
            params.put("sumBindCount", total8);
            params.put("sumIncomeAmt", total9);
            tableModel.addRow(params);
        }
        controller().enable("statistics");
    }

    private void chooseDepart(
            ActionEvent actionEvent
    ) {
        String department = controller().getText("department");
        JTree mgrJTree = controller().get(JTree.class, "mgrJTree");
        TreeModel treeModel = mgrJTree.getModel();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeModel.getRoot();
        selectTreeNodes = new ArrayList<>();
        selectTreeNodes = getTreeNodeByString(treeNode, department);
        TreePath[] treePaths = new TreePath[selectTreeNodes.size()];
        for (int i = 0; i < selectTreeNodes.size(); i++) {
            TreePath treePath = new TreePath(selectTreeNodes.get(i).getPath());
            treePaths[i] = treePath;
        }
        mgrJTree.setSelectionPaths(treePaths);
        mgrJTree.setExpandsSelectedPaths(true);
    }


    private List<DefaultMutableTreeNode> getTreeNodeByString(
            DefaultMutableTreeNode treeNode,
            String str
    ) {
        if (treeNode.getUserObject().toString().contains(str)) {
            selectTreeNodes.add(treeNode);
            for (int i = 0; i < treeNode.getChildCount(); i++) {
                getTreeNodeByString((DefaultMutableTreeNode) treeNode.getChildAt(i), str);
            }
        } else {
            for (int i = 0; i < treeNode.getChildCount(); i++) {
                getTreeNodeByString((DefaultMutableTreeNode) treeNode.getChildAt(i), str);
            }
        }
        return selectTreeNodes;
    }

    private void refreshUser(
            ActionEvent actionEvent
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("if-self", false);
        new CrmProxy().getAllMgrRelations(params)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::searchUserBack, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle);
    }

    private void searchUserBack(
            List<Map<String, Object>> maps
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("uName", "我自己");
        maps.add(params);

        JTree mgrJTree = controller().get(JTree.class, "mgrJTree");
        mgrJTree.setModel(new DefaultTreeModel(null));
        mgrJTree.setRootVisible(false);
        mgrJTree.setModel(new DefaultTreeModelSuffixUtil(maps, "uName", "pName", "department").getDefaultTreeModel());
        mgrJTree.setExpandsSelectedPaths(true);

        DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
        try {
            Image openIcon = ImageIO.read(getClass().getResourceAsStream("/open.jpg"));
            Image closeIcon = ImageIO.read(getClass().getResourceAsStream("/close.jpg"));
            Image leafIcon = ImageIO.read(getClass().getResourceAsStream("/leaf.jpg"));
            defaultTreeCellRenderer.setOpenIcon(new ImageIcon(openIcon));
            defaultTreeCellRenderer.setClosedIcon(new ImageIcon(closeIcon));
            defaultTreeCellRenderer.setLeafIcon(new ImageIcon(leafIcon));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mgrJTree.setCellRenderer(defaultTreeCellRenderer);

        TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
        treeSelectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        mgrJTree.setSelectionModel(treeSelectionModel);
    }


    private void listChanged(
            Object event
    ) {
        final JTable table = controller().get(JTable.class, "list");
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 1) {
            final TypedTableModel tableModel = (TypedTableModel) table.getModel();
            final int selectedRow = table.getSelectedRow();
            final String uName = tableModel.getStringByName(selectedRow, "uName");
            if (!uName.equals("<总计>")) {
                controller().enable("name-list");
            } else {
                controller().disable("name-list");
            }
        } else if (selectedRows.length > 1) {
            controller().disable("name-list");
        } else {
            controller().disable("name-list");
        }
    }
}
