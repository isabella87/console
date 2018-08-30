package com.banhui.console.ui;

import com.banhui.console.rpc.CrmProxy;
import com.banhui.console.rpc.Result;
import org.xx.armory.swing.components.DialogPane;
import org.xx.armory.swing.components.TypedTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xx.armory.swing.UIUtils.UPDATE_UI;

public class CrmChooseManagerDlg
        extends DialogPane {
    private volatile String uName;
    private volatile int role;

    public String getUName() {
        return uName;
    }

    public CrmChooseManagerDlg(
            int role
    ) {
        this.role = role;
        controller().disable("ok");

        final Map<String, Object> params = new HashMap<>();
        params.put("if-self", false);
        new CrmProxy().getAllMgrRelations(params)
                      .thenApplyAsync(Result::list)
                      .thenAcceptAsync(this::searchCallback, UPDATE_UI)
                      .exceptionally(ErrorHandler::handle);
    }

    private void searchCallback(
            List<Map<String, Object>> c
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("uName", "我自己");
        final Map<String, Object> params1 = new HashMap<>();
        params1.put("uName", "全部");
        c.add(params);
        c.add(params1);
        if (role == 1) {
            final Map<String, Object> params2 = new HashMap<>();
            params2.put("uName", "无");
            c.add(params2);
        }
        JTree mgrJTree = controller().get(JTree.class, "mgrJTree");
        mgrJTree.setModel(new DefaultTreeModel(null));
        mgrJTree.setRootVisible(false);
        mgrJTree.setModel(new DefaultTreeModelUtil(c, "uName", "pName").getDefaultTreeModel());
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
        treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mgrJTree.setSelectionModel(treeSelectionModel);

        mgrJTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) mgrJTree.getLastSelectedPathComponent();
            if (treeNode != null && !treeNode.getUserObject().toString().isEmpty()) {
                String uName = treeNode.getUserObject().toString();
                this.uName = uName;
                if (uName != null) {
                    controller().enable("ok");
                }
            } else {
                controller().disable("ok");
            }
        });
    }

    @Override
    public void done(
            int result
    ) {
        if (result == OK) {
            super.done(OK);
        } else {
            super.done(result);
        }
    }
}
