package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.CommandResult;
import cn.gjr.bean.Repository;
import cn.gjr.bean.Selected;
import cn.gjr.utils.GitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        addNode(repo);
        // 同步处理 repositoryList
        base.getRepositoryList().add(repo);
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
        treeModel.removeNodeFromParent(currentNode);
        // 同步处理 repositoryList
        if (GitUtil.isRepository(obj)) {
            // 删除仓库
            Repository rep = (Repository) obj;
            base.getRepositoryList().remove(rep);
        }
    }

    /**
     * 拉取
     */
    void fetch() {
        Selected selection = getSelection();
        selection.getRepositorySet().forEach(e -> {
            CommandResult result = GitUtil.fetch(e.getDir());
            log.info(e.getName() + " fetch " + result.isSuccess());
        });
        reloadTree();
    }

    /**
     * 变基
     */
    void rebase() {
        Selected selection = getSelection();
        selection.getBranchSet().forEach(e -> {
            CommandResult result = GitUtil.rebase(e);
            log.info(e.getName() + " rebase " + result.isSuccess());
        });
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
}
