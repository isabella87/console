package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xx.armory.swing.DialogUtils;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.InternalFramePane;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.booleanValue;
import static com.banhui.console.rpc.ResultUtils.stringValue;
import static org.xx.armory.swing.ComponentUtils.showModel;
import static org.xx.armory.swing.DialogUtils.confirm;
import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class BrowseClientMgrManagementFrame
        extends InternalFramePane {

    private final Logger logger = LoggerFactory.getLogger(BrowseClientMgrManagementFrame.class);

    private List<Map<String, Object>> mgrTreeData;
    private DefaultMutableTreeNode selectTreeNode;
    private String pgsUname;

    /**
     * {@inheritDoc}
     */
    public BrowseClientMgrManagementFrame() {
        controller().connect("create", this::create);
        controller().connect("delete", this::delete);
        controller().connect("designatedSuperior", this::designatedSuperior);
        controller().connect("edit", this::edit);
        controller().connect("updateCode", this::updateCode);
        controller().connect("refresh", this::refresh);
        controller().connect("gps", this::gps);


        controller().readOnly("department", true);
        controller().readOnly("position", true);
        controller().readOnly("enabled", true);
        controller().readOnly("rCode", true);

        controller().disable("delete");
        controller().disable("designatedSuperior");
        controller().disable("edit");
        controller().disable("updateCode");

        controller().call("refresh");
    }


    /**
     * 主动定位节点事件
     *
     * @param actionEvent
     */
    private void gps(ActionEvent actionEvent) {
        String uName = controller().getText("u-name");
        if (uName.isEmpty()) {
            confirm(null, controller().getMessage("uname-confirm"));
        } else {
            JTree mgrJTree = controller().get(JTree.class, "mgrJTree");
            TreeModel treeModel = mgrJTree.getModel();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeModel.getRoot();
            selectTreeNode = null;
            treeNode = getTreeNodeByUname(treeNode, uName);
            if (selectTreeNode == null) {
                confirm(null, controller().getMessage("cannot-search-uname-confirm"));
            } else {
                mgrJTree.setSelectionPath(new TreePath(treeNode.getPath()));
                mgrJTree.setExpandsSelectedPaths(true);
            }
        }
    }

    /**
     * 用于创建、编译、指定上级、更改编码后对作用节点的自动展开并定位
     *
     * @param uName
     */
    private void expandTheDescNode(String uName) {
        JTree mgrJTree = controller().get(JTree.class, "mgrJTree");
        TreeModel treeModel = mgrJTree.getModel();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeModel.getRoot();

        if (uName != null && !uName.isEmpty()) {

            selectTreeNode = null;
            treeNode = getTreeNodeByUname(treeNode, uName);
            if (selectTreeNode != null) {
                mgrJTree.setSelectionPath(new TreePath(treeNode.getPath()));
                mgrJTree.setExpandsSelectedPaths(true);
            }
        } else {
            treeNode = (DefaultMutableTreeNode) treeNode.getChildAt(0);
            mgrJTree.setSelectionPath(new TreePath(treeNode.getPath()));
            mgrJTree.setExpandsSelectedPaths(true);
        }

    }

    private DefaultMutableTreeNode getTreeNodeByUname(
            DefaultMutableTreeNode treeNode,
            String uName
    ) {

        if (!treeNode.getUserObject().toString().equals(uName)) {
            for (int j = 0; j < treeNode.getChildCount(); j++) {
                getTreeNodeByUname((DefaultMutableTreeNode) treeNode.getChildAt(j), uName);
            }
        } else {
            selectTreeNode = treeNode;
            return selectTreeNode;
        }
        return selectTreeNode;
    }

    private void refresh(ActionEvent actionEvent) {
        Map<String, Object> params = new HashMap<>();
        params.put("if-self", true);
        new CrmProxy().getAllMgrRelations(params).thenApplyAsync(Result::list).whenCompleteAsync(this::callback, UPDATE_UI);
    }

    private void updateCode(ActionEvent actionEvent) {

        if (confirm(null, controller().getMessage("update-rcode-confirm"))) {
            JTree jTree = controller().get(JTree.class, "mgrJTree");
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
            String uname = treeNode.getUserObject().toString();
            this.pgsUname = uname;
            String remark = DialogUtils.inputText(null, controller().formatMessage("rcode-confirm", uname), "");
            if (remark != null && !remark.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("u-name", uname);
                params.put("r-code", remark);
                new CrmProxy().updateCrmMgrRcode(params)
                              .whenCompleteAsync(this::updateCrmMgrRcodeCallback, UPDATE_UI);
            }
        }
    }

    private void updateCrmMgrRcodeCallback(
            Result result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().call("refresh");
        }
    }

    private void edit(ActionEvent actionEvent) {
        JTree jTree = controller().get(JTree.class, "mgrJTree");
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        String uName = treeNode.getUserObject().toString();

        EditCrmMgrDlg editCrmMgrDlg = new EditCrmMgrDlg(uName);
        editCrmMgrDlg.setFixedSize(false);

        if (showModel(null, editCrmMgrDlg) == DialogPane.OK) {
            this.pgsUname = editCrmMgrDlg.getUname();
            controller().call("refresh");


        }
    }

    private void designatedSuperior(ActionEvent actionEvent) {
        JTree jTree = controller().get(JTree.class, "mgrJTree");
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        String uName = treeNode.getUserObject().toString();
        this.pgsUname = uName;
        String remark = DialogUtils.inputText(null, controller().formatMessage("designated-superior-confirm", uName), "");
        if (remark != null && !remark.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("u-name", uName);
            params.put("p-name", remark);
            new CrmProxy().moveCrmMgrRelation(params)
                          .whenCompleteAsync(this::designatedSuperiorCallback, UPDATE_UI);
        }
    }

    private void designatedSuperiorCallback(
            Result result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            controller().call("refresh");
        }

    }


    private void delete(ActionEvent actionEvent) {
        JTree jTree = controller().get(JTree.class, "mgrJTree");
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        String uname = treeNode.getUserObject().toString();
        if (confirm(null, controller().formatMessage("del-confirm", uname))) {

            new CrmProxy().deleteCrmMgrRelation(uname)
                          .whenCompleteAsync(this::delCallback, UPDATE_UI);
        }

    }

    private void delCallback(
            Result result,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.pgsUname = null;
            controller().call("refresh");
        }
    }

    private void create(ActionEvent actionEvent) {
        CreateCrmMgrDlg createCrmMgrDlg = new CreateCrmMgrDlg();
        createCrmMgrDlg.setFixedSize(false);

        if (showModel(null, createCrmMgrDlg) == DialogPane.OK) {
            this.pgsUname = createCrmMgrDlg.getuName();
            controller().call("refresh");
        }
    }

    private void callback(
            List<Map<String, Object>> maps,
            Throwable t
    ) {
        if (t != null) {
            ErrorHandler.handle(t);
        } else {
            this.mgrTreeData = maps;

            controller().disable("delete");
            controller().disable("designatedSuperior");
            controller().disable("edit");
            controller().disable("updateCode");

            updateDetailInfo();

            JScrollPane jScrollPane = controller().get(JScrollPane.class, "mgrJTree");
//            mgrJTree.setModel(new DefaultTreeModel(null));
            CheckNodeTree mgrJTree = new CheckNodeTree(new DefaultTreeModelUtil(mgrTreeData, "uName", "pName").getDefaultTreeModel());



            mgrJTree.setRootVisible(false);
//            mgrJTree.setModel(new DefaultTreeModelUtil(mgrTreeData, "uName", "pName").getDefaultTreeModel());
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
//            mgrJTree.setCellRenderer(defaultTreeCellRenderer);

            TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
            treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            mgrJTree.setSelectionModel(treeSelectionModel);

            jScrollPane.setViewportView(mgrJTree);

            mgrJTree.addTreeSelectionListener(e -> {
                CheckNode treeNode = (CheckNode) mgrJTree.getLastSelectedPathComponent();
                if (treeNode != null && !treeNode.getUserObject().toString().isEmpty()) {
                    String uName = treeNode.getUserObject().toString();
                    this.pgsUname = uName;
                    Map<String, Object> map = getUserInfoByUname(uName);
                    if (map != null) {
                        updateDetailInfo();

                        controller().enable("delete");
                        controller().enable("designatedSuperior");
                        controller().enable("edit");
                        controller().enable("updateCode");
                    }
                } else {
                    controller().disable("delete");
                    controller().disable("designatedSuperior");
                    controller().disable("edit");
                    controller().disable("updateCode");
                }
            });

            expandTheDescNode(pgsUname);

        }
    }

    private void updateDetailInfo() {
        JTree jTree = controller().get(JTree.class, "mgrJTree");
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        if (treeNode == null) {
            return;
        }
        String uname = treeNode.getUserObject().toString();

        Map<String, Object> map = getUserInfoByUname(uname);
        if (map != null) {
            controller().setText("department", stringValue(map, "department"));
            controller().setText("position", stringValue(map, "position"));
            controller().setText("enabled", booleanValue(map, "enabled") != null && booleanValue(map, "enabled") == true ? "已启用" : "未启用");
            controller().setText("rCode", stringValue(map, "rCode"));
        }
    }


    private Map<String, Object> getUserInfoByUname(String uName) {
        for (Map<String, Object> map : mgrTreeData) {
            String myUName = stringValue(map, "uName");
            if (myUName != null && !myUName.isEmpty() && myUName.equals(uName)) {
                return map;
            }
        }
        return null;
    }
}
