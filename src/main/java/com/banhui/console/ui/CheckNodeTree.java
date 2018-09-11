package com.banhui.console.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class CheckNodeTree extends JTree {
    public CheckNodeTree() {
        this(new DefaultTreeModel(null));
    }

    public CheckNodeTree(TreeModel model) {
        super(model);

        setCellRenderer(new CheckRenderer());
      /*  getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION
        );*/
        addMouseListener(new NodeSelectionListener(this));
    }

    public void setModel(TreeModel treeModel) {
        super.setModel(treeModel);
    }

    class NodeSelectionListener extends MouseAdapter {
        JTree tree;

        NodeSelectionListener(JTree tree) {
            this.tree = tree;
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);


            if (path != null) {
                if(tree.getSelectionModel().getSelectionMode() == TreeSelectionModel.SINGLE_TREE_SELECTION){
                    cancelSelectAllForSingleSelectExcCur((DefaultMutableTreeNode) tree.getModel().getRoot(), path);
                }

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node instanceof CheckNode) {
                    CheckNode chNode = (CheckNode) node;
                    boolean isSelected = !(chNode.isSelected());
                    chNode.setSelected(isSelected);
                    if (chNode.getSelectionMode() == CheckNode.DIG_IN_SELECTION) {
                        if (isSelected) {
                            tree.expandPath(path);
                        } else {
                            tree.collapsePath(path);
                        }
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeChanged(chNode);
                    tree.revalidate();
                    tree.repaint();
                }
            }
        }

        private void cancelSelectAllForSingleSelectExcCur(
                DefaultMutableTreeNode node,
                TreePath curTreePath
        ) {
            for (int i = 0; i < node.getChildCount(); i++) {
                cancelSelectAllForSingleSelectExcCur((DefaultMutableTreeNode) node.getChildAt(i), curTreePath);
            }

            if ((node instanceof CheckNode) && !node.getUserObject().toString().equals(curTreePath.getLastPathComponent().toString())) {
                CheckNode chNode = (CheckNode) node;
                chNode.setSelected(false);
                ((DefaultTreeModel) tree.getModel()).nodeChanged(chNode);
            }
        }
    }

    public static void main(String args[]) {
        String[] strs = {"swing",     // 0
                "platf",     // 1
                "basic",     // 2
                "metal",     // 3
                "JTree"};    // 4

        CheckNode[] nodes = new CheckNode[strs.length];
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        for (int i = 0; i < strs.length; i++) {
            nodes[i] = new CheckNode(strs[i]);
        }
        root.add(nodes[0]);
        nodes[0].add(nodes[1]);
        nodes[1].add(nodes[2]);
        nodes[1].add(nodes[3]);
        nodes[0].add(nodes[4]);
        DefaultTreeModel model = new DefaultTreeModel(root);
        CheckNodeTree tree = new CheckNodeTree();
        tree.setModel(model);
        JFrame frame = new JFrame("测试");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(tree);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(jsp, BorderLayout.CENTER);
        frame.setSize(300, 200);
        frame.setVisible(true);
    }
}

class CheckRenderer extends JPanel implements TreeCellRenderer {
    protected JCheckBox check;

    protected TreeLabel label;
    DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();

    public CheckRenderer() {
        setLayout(null);
        add(check = new JCheckBox());
        add(label = new TreeLabel());
        check.setBackground(UIManager.getColor("Tree.textBackground"));
        label.setForeground(UIManager.getColor("Tree.textForeground"));
    }

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean isSelected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus
    ) {
        if (!(value instanceof CheckNode)) {
            return dtcr.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
        }
        String stringValue = tree.convertValueToText(value, isSelected,
                                                     expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
        check.setSelected(((CheckNode) value).isSelected());
        label.setFont(tree.getFont());
        label.setText(stringValue);
        label.setSelected(isSelected);
        label.setFocus(hasFocus);

        Image leafIcon = null;
        Image openIcon = null;
        Image closeIcon = null;

        try {
            leafIcon = ImageIO.read(getClass().getResourceAsStream("/leaf.jpg"));
            openIcon = ImageIO.read(getClass().getResourceAsStream("/open.jpg"));
            closeIcon = ImageIO.read(getClass().getResourceAsStream("/close.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (leaf) {
            label.setIcon(new ImageIcon(leafIcon));
//            label.setIcon(UIManager.getIcon("Tree.leafIcon"));
        } else if (expanded) {
            label.setIcon(new ImageIcon(openIcon));
//            label.setIcon(UIManager.getIcon("Tree.openIcon"));
        } else {
            label.setIcon(new ImageIcon(closeIcon));
//            label.setIcon(UIManager.getIcon("Tree.closedIcon"));
        }
        return this;
    }

    public Dimension getPreferredSize() {
        Dimension d_check = check.getPreferredSize();
        Dimension d_label = label.getPreferredSize();
        return new Dimension(d_check.width + d_label.width,
                             (d_check.height < d_label.height ? d_label.height
                                     : d_check.height));
    }

    public void doLayout() {
        Dimension d_check = check.getPreferredSize();
        Dimension d_label = label.getPreferredSize();
        int y_check = 0;
        int y_label = 0;
        if (d_check.height < d_label.height) {
            y_check = (d_label.height - d_check.height) / 2;
        } else {
            y_label = (d_check.height - d_label.height) / 2;
        }
        check.setLocation(0, y_check);
        check.setBounds(0, y_check, d_check.width, d_check.height);
        label.setLocation(d_check.width, y_label);
        label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
    }

    public void setBackground(Color color) {
        if (color instanceof ColorUIResource)
            color = null;
        super.setBackground(color);
    }

    public class TreeLabel extends JLabel {
        boolean isSelected;

        boolean hasFocus;

        public TreeLabel() {
        }

        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
                color = null;
            super.setBackground(color);
        }

        public void paint(Graphics g) {
            String str;
            if ((str = getText()) != null) {
                if (0 < str.length()) {
                    if (isSelected) {
                        g.setColor(UIManager
                                           .getColor("Tree.selectionBackground"));
                    } else {
                        g.setColor(UIManager.getColor("Tree.textBackground"));
                    }

                    Dimension d = getPreferredSize();
                    int imageOffset = 0;
                    Icon currentI = getIcon();
                    if (currentI != null) {
                        imageOffset = currentI.getIconWidth()
                                + Math.max(0, getIconTextGap() - 1);
                    }
                    g.fillRect(imageOffset, 0, d.width - 1 - imageOffset,
                               d.height);
                    if (hasFocus) {
                        g.setColor(UIManager
                                           .getColor("Tree.selectionBorderColor"));
                        g.drawRect(imageOffset, 0, d.width - 1 - imageOffset,
                                   d.height - 1);
                    }
                }
            }
            super.paint(g);
        }

        public Dimension getPreferredSize() {
            Dimension retDimension = super.getPreferredSize();
            if (retDimension != null) {
                retDimension = new Dimension(retDimension.width + 3,
                                             retDimension.height);
            }
            return retDimension;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public void setFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }
    }


}
