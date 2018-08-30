package com.banhui.console.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.banhui.console.rpc.ResultUtils.stringValue;

/**
 *
 */
public class DefaultTreeModelSuffixUtil {
    /**
     * 默认根节点显示名称（根节点通常设置为不可见，以便界面可见树第一级可以展示多个节点）
     */
    private final static String rootNodeTitle = "root";
    /**
     * 树的数据源
     */
    private List<Map<String, Object>> treeData;
    /**
     * 各树节点需要展示的字段名
     */
    private String treeNodeTitle;
    /**
     * 各树节点需要展示的后缀名
     */
    private String suffix;
    /**
     * 各树节点父节点的字段名
     */
    private String pTreeNodeTitle;

    public DefaultTreeModelSuffixUtil(
            List<Map<String, Object>> treeData,
            String treeNodeTitle,
            String pTreeNodeTitle,
            String suffix
    ) {
        this.treeData = treeData;
        this.treeNodeTitle = treeNodeTitle;
        this.pTreeNodeTitle = pTreeNodeTitle;
        this.suffix = suffix;
    }

    public DefaultTreeModel getDefaultTreeModel() {
        DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(rootNodeTitle);
        // 1, 遍历出顶级节点集合； 2，循环顶级节点并遍历创建子节点
        List<Map<String, Object>> topMaps = getTopMaps(treeData);
        for (Map<String, Object> map : topMaps) {
            DefaultMutableTreeNode treeNode;
            String str = stringValue(map, suffix);
            if (str != null && !str.isEmpty()) {
                treeNode = new DefaultMutableTreeNode(stringValue(map, treeNodeTitle) + "(" + str + ")");
            } else {
                treeNode = new DefaultMutableTreeNode(stringValue(map, treeNodeTitle));
            }
            rootTreeNode.add(treeNode);
            addChildTreeNode(treeNode, stringValue(map, treeNodeTitle));
        }
        return new DefaultTreeModel(rootTreeNode);
    }

    private void addChildTreeNode(
            DefaultMutableTreeNode pTreeNode,
            String pName
    ) {
        for (Map<String, Object> map : treeData) {
            String myPName = stringValue(map, pTreeNodeTitle);
            if (myPName != null && !myPName.isEmpty() && myPName.equals(pName)) {
                DefaultMutableTreeNode treeNode;
                String str = stringValue(map, suffix);
                if (str != null && !str.isEmpty()) {
                    treeNode = new DefaultMutableTreeNode(stringValue(map, treeNodeTitle) + "(" + str + ")");
                } else {
                    treeNode = new DefaultMutableTreeNode(stringValue(map, treeNodeTitle));
                }
                pTreeNode.add(treeNode);
                addChildTreeNode(treeNode, stringValue(map, treeNodeTitle));
            }
        }
    }

    private List<Map<String, Object>> getTopMaps(List<Map<String, Object>> maps) {
        List<Map<String, Object>> topMaps = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            String pName = stringValue(map, pTreeNodeTitle);
            if (pName == null || pName.isEmpty()) {
                topMaps.add(map);
            }
        }
        return topMaps;
    }

}
