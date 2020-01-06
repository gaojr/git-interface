package cn.gjr;

import cn.gjr.bean.Repository;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

/**
 * 动态树
 *
 * @author GaoJunru
 */
public class DynamicTree extends JPanel {
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private List<Repository> repositoryList;

    DynamicTree() {
        super(new GridLayout(1, 0));

        rootNode = new DefaultMutableTreeNode("仓库");
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new Listener());
        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    /**
     * 增加节点
     */
    void add() {
        // TODO 增加节点
        // TODO 同步处理 repositoryList
    }

    /**
     * 移除节点
     */
    void remove() {
        // TODO 同步处理 repositoryList
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            // 有选择的节点
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            TreeNode parent = currentNode.getParent();
            if (parent != null) {
                // 不是根节点
                treeModel.removeNodeFromParent(currentNode);
            }
        }
    }

    /**
     * 拉取
     */
    void fetch() {
        // TODO 拉取
        // TODO 同步处理 repositoryList
    }

    /**
     * 变基
     */
    void rebase() {
        // TODO 变基
        // TODO 同步处理 repositoryList
    }

    /**
     * 新增节点
     *
     * @param parent 父节点（为null时为根节点）
     * @param child 子节点对象
     * @param visible 是否显示
     * @return 树节点
     */
    DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean visible) {
        if (parent == null) {
            parent = rootNode;
        }
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        // It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        if (visible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    static class Listener implements TreeModelListener {
        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            // TODO ?
//            List<Repository> repositoryList = this.repositoryList;
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed node is the child of the node we've already gotten.
             * Otherwise, the changed node and the specified node are the same.
             */

//            int index = e.getChildIndices()[0];
//            node = (DefaultMutableTreeNode) (node.getChildAt(index));
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            // TODO ?
//            List<Repository> repositoryList = this.repositoryList;
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            // TODO ?
//            List<Repository> repositoryList = this.repositoryList;
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            // TODO ?
//            List<Repository> repositoryList = this.repositoryList;
        }
    }

    public void setRepositoryList(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }
}
