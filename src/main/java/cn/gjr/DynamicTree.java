package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Repository;
import cn.gjr.bean.Selected;
import cn.gjr.task.FetchTask;
import cn.gjr.task.Pool;
import cn.gjr.task.RebaseTask;
import cn.gjr.utils.GitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 动态树
 *
 * @author GaoJunru
 */
@Slf4j
public class DynamicTree extends JPanel {
    private transient Base base;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTree tree;

    DynamicTree(Base base) {
        super(new GridLayout(1, 0));
        this.base = base;

        rootNode = new DefaultMutableTreeNode("仓库");
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new NodeListener());

        tree = new JTree(treeModel);
        // 根节点不可见
        tree.setRootVisible(false);
        // 显示树延伸线
        tree.setShowsRootHandles(true);
        // 不可编辑
        tree.setEditable(false);
        // 可多选
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // 可拖动
        tree.setDropMode(DropMode.ON);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new DragHandler());
        tree.addMouseListener(new DragListener());

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    /**
     * 增加节点
     */
    void add() {
        new ChooseFrame(this);
    }

    /**
     * 增加
     *
     * @param repo 仓库
     */
    void add(Repository repo) {
        // 同步处理 repositoryList
        base.getRepositoryList().add(repo);
        // 增加节点
        addNode(repo);
    }

    /**
     * 移除节点
     */
    void remove() {
        int count = tree.getSelectionCount();
        TreePath currentSelection = tree.getSelectionPath();
        if (count != 1 || currentSelection == null) {
            return;
        }
        // 有选择的节点
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
        Object obj = currentNode.getUserObject();
        if (GitUtil.isBranch(obj)) {
            // 是分支对象
            return;
        }
        // 只删除仓库
        if (GitUtil.isRepository(obj)) {
            Repository rep = (Repository) obj;
            // 同步处理 repositoryList
            base.getRepositoryList().remove(rep);
            // 移除节点
            treeModel.removeNodeFromParent(currentNode);
        }
    }

    /**
     * 拉取
     */
    void fetch() {
        Selected selection = getSelection();
        Set<Repository> repositorySet = selection.getRepositorySet();
        Pool pool = new Pool(repositorySet.size());
        repositorySet.forEach(e -> {
            FetchTask task = new FetchTask(e);
            pool.add(task);
        });
        pool.run();
        reloadTree();
    }

    /**
     * 变基
     */
    void rebase() {
        Selected selection = getSelection();
        Set<Branch> branchSet = selection.getBranchSet();
        Pool pool = new Pool(branchSet.size());
        branchSet.forEach(e -> {
            RebaseTask task = new RebaseTask(e);
            pool.add(task);
        });
        pool.run();
        reloadTree();
    }

    /**
     * 获取已选的仓库列表和分支列表
     *
     * @return 选择对象
     */
    private Selected getSelection() {
        TreePath[] paths = getSelectedPaths();
        List<Repository> repositoryList = new ArrayList<>(paths.length);
        List<Branch> branchList = new ArrayList<>(paths.length);
        for (TreePath path : paths) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (node.isRoot()) {
                // 是根节点
                return new Selected(base.getRepositoryList(), Collections.emptyList());
            }
            Object obj = node.getUserObject();
            if (node.isLeaf()) {
                // 是分支
                Branch b = (Branch) obj;
                branchList.add(b);
                continue;
            }
            int depth = path.getPathCount();
            if (depth == 2) {
                // 是仓库
                Repository r = (Repository) obj;
                repositoryList.add(r);
            }
        }
        return new Selected(repositoryList, branchList);
    }

    /**
     * 获取已选的树路径
     *
     * @return 树路径数组
     */
    private TreePath[] getSelectedPaths() {
        TreePath[] paths = tree.getSelectionPaths();
        if (ArrayUtils.isEmpty(paths)) {
            return new TreePath[0];
        }
        return paths;
    }

    /**
     * 重新加载树
     */
    private void reloadTree() {
        // 同步处理 repositoryList
        GitUtil.generateRepositoryList(base.getRepositoryList());
        // 修改树
        rootNode.removeAllChildren();
        createTree(base.getRepositoryList());
        // 刷新树
        treeModel.reload();
    }

    /**
     * 生成树
     *
     * @param repositoryList 仓库列表
     */
    void createTree(List<Repository> repositoryList) {
        for (Repository repository : repositoryList) {
            addNode(repository);
        }
    }

    /**
     * 增加节点
     *
     * @param repo 仓库
     */
    private void addNode(Repository repo) {
        DefaultMutableTreeNode rNode = addObject(null, repo, true);
        for (Branch branch : repo.getBranchList()) {
            addObject(rNode, branch, false);
        }
    }

    /**
     * 新增节点
     *
     * @param parent 父节点（为null时为根节点）
     * @param child 子节点对象
     * @param visible 是否显示
     * @return 树节点
     */
    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean visible) {
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

    /**
     * 设置渲染器
     *
     * @param render 树节点渲染器
     */
    void setRenderer(TreeCellRenderer render) {
        tree.setCellRenderer(render);
    }

    static class NodeListener implements TreeModelListener {
        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            // do nothing
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            // do nothing
        }

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            // do nothing
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            // do nothing
        }
    }

    private static class DragHandler extends TransferHandler {
    }

    class DragListener implements MouseListener {
        /**
         * 节点路径
         */
        private TreePath nodePath;

        @Override
        public void mousePressed(MouseEvent e) {
            // 按下鼠标时候获得被拖动的节点
            TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
            if (tp != null) {
                nodePath = tp;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // 鼠标松开时获得需要拖到哪个父节点
            TreePath toPath = tree.getPathForLocation(e.getX(), e.getY());
            if (toPath == null || nodePath == null || toPath == nodePath) {
                return;
            }
            // 阻止向子节点拖动
            if (nodePath.isDescendant(toPath)) {
                JOptionPane.showMessageDialog(tree, "无法移动！", "非法操作", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DefaultMutableTreeNode fromNode = (DefaultMutableTreeNode) nodePath.getLastPathComponent();
            DefaultMutableTreeNode toNode = (DefaultMutableTreeNode) toPath.getLastPathComponent();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) toNode.getParent();
            int index = parent.getIndex(toNode);
            parent.insert(fromNode, index);
            nodePath = null;
            treeModel.reload();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // do nothing
        }
    }
}
