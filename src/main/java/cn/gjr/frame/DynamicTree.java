package cn.gjr.frame;

import cn.gjr.Base;
import cn.gjr.bean.Branch;
import cn.gjr.bean.Repository;
import cn.gjr.bean.Selected;
import cn.gjr.task.FetchTask;
import cn.gjr.task.Pool;
import cn.gjr.task.RebaseTask;
import cn.gjr.utils.GitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
    /**
     * 根节点
     */
    private DefaultMutableTreeNode rootNode;
    /**
     * 默认组节点
     */
    private DefaultMutableTreeNode defaultNode;
    /**
     * 树模型
     */
    private DefaultTreeModel treeModel;
    /**
     * 树
     */
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
     * 添加默认组节点
     */
    void createDefaultNode() {
        defaultNode = addObject(rootNode, "默认", true);
    }

    /**
     * 增加分组
     */
    void addGroup() {
        // TODO 获取分组名称
        String groupName = "分组";
        addObject(rootNode, groupName, true);
    }

    /**
     * 增加仓库节点
     */
    void addRepo() {
        new ChooseFrame(this);
    }

    /**
     * 增加
     *
     * @param repo 仓库
     */
    void addRepo(Repository repo) {
        // 同步处理 repositoryList
        base.getRepositories().add(repo);
        // 增加节点
        addNode(repo);
    }

    /**
     * 移除
     */
    void remove() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection == null) {
            return;
        }
        if (tree.getSelectionCount() != 1) {
            JOptionPane.showMessageDialog(tree, "只能移除单个节点！", "非法操作", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 有选择的节点
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
        Object obj = currentNode.getUserObject();
        if (GitUtil.isBranch(obj)) {
            // 是分支对象
            JOptionPane.showMessageDialog(tree, "不能移除分支！", "非法操作", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 只删除仓库
        if (GitUtil.isRepository(obj)) {
            Repository rep = (Repository) obj;
            // 同步处理 repositoryList
            base.getRepositories().remove(rep);
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
        if (CollectionUtils.isEmpty(repositorySet)) {
            return;
        }
        Pool pool = new Pool(repositorySet.size());
        repositorySet.stream().map(FetchTask::new).forEach(pool::add);
        pool.run();
        reloadTree();
    }

    /**
     * 变基
     */
    void rebase() {
        Selected selection = getSelection();
        Set<Branch> branchSet = selection.getBranchSet();
        if (CollectionUtils.isEmpty(branchSet)) {
            return;
        }
        Pool pool = new Pool(branchSet.size());
        branchSet.stream().map(RebaseTask::new).forEach(pool::add);
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
                return new Selected(base.getRepositories(), Collections.emptyList());
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
        GitUtil.generateRepositoryList(base.getRepositories());
        // 修改树
        rootNode.removeAllChildren();
        createDefaultNode();
        createTree(base.getRepositories());
        // 刷新树
        treeModel.reload();
        expandTree();
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
        DefaultMutableTreeNode parent = defaultNode;
        if (StringUtils.isNoneBlank(repo.getGroup())) {
            parent = addObject(rootNode, repo.getGroup(), true);
        }
        DefaultMutableTreeNode rNode = addObject(parent, repo, true);
        for (Branch branch : repo.getBranchList()) {
            addObject(rNode, branch, true);
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

    /**
     * 展开到叶子节点
     */
    public void expandTree() {
        int rowCount = tree.getRowCount();
        tree.expandRow(rowCount - 1);
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
        public void mouseReleased(MouseEvent event) {
            // 鼠标松开时获得需要拖到哪个父节点
            TreePath toPath = tree.getPathForLocation(event.getX(), event.getY());
            if (toPath == null || nodePath == null || toPath == nodePath) {
                return;
            }
            // 阻止向子节点拖动
            if (nodePath.isDescendant(toPath)) {
                JOptionPane.showMessageDialog(tree, "无法移动！", "非法操作", JOptionPane.WARNING_MESSAGE);
                return;
            }
            DefaultMutableTreeNode fromNode = (DefaultMutableTreeNode) nodePath.getLastPathComponent();
            int fromDepth = nodePath.getPathCount();
            if (fromDepth == 4) {
                // 不能移动分支节点
                JOptionPane.showMessageDialog(tree, "无法移动！", "非法操作", JOptionPane.WARNING_MESSAGE);
                return;
            }
            DefaultMutableTreeNode toNode = (DefaultMutableTreeNode) toPath.getLastPathComponent();
            int toDepth = toPath.getPathCount();
            if (toDepth < fromDepth) {
                toNode.add(fromNode);
            } else if (toDepth == fromDepth) {
                // 同级移动
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) toNode.getParent();
                int index = parent.getIndex(toNode);
                parent.insert(fromNode, index);
            }
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
