package cn.gjr.frame;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Node;
import cn.gjr.bean.Repository;
import cn.gjr.bean.Selected;
import cn.gjr.task.FetchTask;
import cn.gjr.task.Pool;
import cn.gjr.task.RebaseTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.*;

/**
 * 动态树
 *
 * @author GaoJunru
 */
@Slf4j
public class DynamicTree extends JPanel {
    /**
     * 默认组名
     */
    private static final String DEFAULT_GROUP = "默认";
    /**
     * 根节点
     */
    private Node rootNode;
    /**
     * 默认组节点
     */
    private Node defaultNode;
    /**
     * 树模型
     */
    private DefaultTreeModel treeModel;
    /**
     * 树
     */
    private JTree tree;
    /**
     * 分组节点
     */
    private Map<String, Node> groupMap = new HashMap<>();

    /**
     * 构造函数
     */
    public DynamicTree() {
        super(new GridLayout(1, 0));

        rootNode = new Node("仓库");
        rootNode.setType(Node.TYPE_ROOT);
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
     * 添加组节点
     *
     * @param groups 分组
     */
    void createGroupNode(List<String> groups) {
        defaultNode = addObject(rootNode, DEFAULT_GROUP);
        groups.forEach(e -> groupMap.put(e, addObject(rootNode, e)));
        groupMap.put(null, defaultNode);
        groupMap.put("", defaultNode);
        groupMap.put(DEFAULT_GROUP, defaultNode);
    }

    /**
     * 增加分组
     */
    void addGroup() {
        new GroupFrame(this);
    }

    /**
     * 增加分组
     */
    void addGroup(String groupName) {
        groupName = StringUtils.trim(groupName);
        if (StringUtils.isBlank(groupName)) {
            JOptionPane.showMessageDialog(tree, "分组名不可为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (groupMap.containsKey(groupName)) {
            JOptionPane.showMessageDialog(tree, "分组名不可重复！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Node node = addObject(rootNode, groupName);
        groupMap.put(groupName, node);
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
        // 增加节点
        addNode(repo);
    }

    /**
     * 移除
     */
    void remove() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection == null) {
            // 没有选择的节点
            return;
        }
        if (tree.getSelectionCount() != 1) {
            // 选择了多个节点
            JOptionPane.showMessageDialog(tree, "只能移除单个节点！", "非法操作", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Node currentNode = (Node) (currentSelection.getLastPathComponent());
        if (currentNode.getType() == Node.TYPE_BRANCH) {
            // 是分支节点
            JOptionPane.showMessageDialog(tree, "不能移除分支！", "非法操作", JOptionPane.ERROR_MESSAGE);
        } else if (currentNode.getType() == Node.TYPE_REPO) {
            // 是仓库节点
            treeModel.removeNodeFromParent(currentNode);
        } else if (defaultNode.equals(currentNode)) {
            // 是默认分组节点
            JOptionPane.showMessageDialog(tree, "不能移除默认分组！", "非法操作", JOptionPane.ERROR_MESSAGE);
        } else {
            // 是分组节点
            int value = JOptionPane.showConfirmDialog(tree, "分组下的仓库也会被移除", "警告", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (JOptionPane.OK_OPTION == value) {
                treeModel.removeNodeFromParent(currentNode);
            }
        }
    }

    /**
     * TODO 拉取
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
     * TODO 变基
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
     * TODO 获取已选的仓库列表和分支列表
     *
     * @return 选择对象
     */
    private Selected getSelection() {
        TreePath[] paths = getSelectedPaths();
        List<Repository> repositoryList = new ArrayList<>(paths.length);
        List<Branch> branchList = new ArrayList<>(paths.length);
        for (TreePath path : paths) {
            Node node = (Node) path.getLastPathComponent();
            if (node.isRoot()) {
                // 是根节点
                return new Selected(node.getRepositoryList(), Collections.emptyList());
            }
            Object obj = node.getUserObject();
            if (GitUtil.isBranch(obj)) {
                // 是分支
                Branch b = (Branch) obj;
                branchList.add(b);
            } else if (GitUtil.isRepository(obj)) {
                // 是仓库
                Repository r = (Repository) obj;
                repositoryList.add(r);
            } else {
                // 是分组
                repositoryList.addAll(node.getRepositoryList());
            }
            repositoryList.addAll(node.getRepositoryList());
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
        // 刷新树
        treeModel.reload();
        expandTree();
    }

    /**
     * 生成树
     *
     * @param repositories 仓库
     */
    void createTree(List<Repository> repositories) {
        for (Repository repository : repositories) {
            addNode(repository);
        }
    }

    /**
     * 增加节点
     *
     * @param repo 仓库
     */
    private void addNode(Repository repo) {
        Node parent = groupMap.getOrDefault(repo.getGroup(), defaultNode);
        Node rNode = addObject(parent, repo);
        for (Branch branch : repo.getBranchList()) {
            addObject(rNode, branch);
        }
    }

    /**
     * 新增节点
     *
     * @param parent 父节点（为null时为根节点）
     * @param child 子节点对象
     * @return 树节点
     */
    private Node addObject(Node parent, Object child) {
        if (parent == null) {
            parent = rootNode;
        }
        Node childNode = new Node(child);
        // It is key to invoke this on the TreeModel, and NOT Node
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
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
        expandAll(new TreePath(rootNode));
    }

    /**
     * 展开全部
     *
     * @param parentPath 父节点
     */
    private void expandAll(TreePath parentPath) {
        Node parent = (Node) parentPath.getLastPathComponent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            Node node = (Node) parent.getChildAt(i);
            expandAll(parentPath.pathByAddingChild(node));
        }
        tree.expandPath(parentPath);
    }

    /**
     * 节点转仓库list
     *
     * @return 仓库list
     */
    public List<Repository> getRepositories() {
        return rootNode.getRepositoryList();
    }

    /**
     * 节点转分组list
     *
     * @return 分组list
     */
    public List<String> getGroups() {
        List<String> list = rootNode.getGroupList();
        list.remove(DEFAULT_GROUP);
        list.remove("");
        list.remove(null);
        return list;
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
            nodePath = null;
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
                JOptionPane.showMessageDialog(tree, "无法移动！", "非法操作", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Node fromNode = (Node) nodePath.getLastPathComponent();
            int fromDepth = nodePath.getPathCount();
            if (fromDepth == 4) {
                // 排除分支节点
                JOptionPane.showMessageDialog(tree, "无法移动！", "非法操作", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Node toNode = (Node) toPath.getLastPathComponent();
            int toDepth = toPath.getPathCount();
            if (toDepth < fromDepth) {
                // 下级->上级
                toNode.add(fromNode);
            } else if (toDepth == fromDepth) {
                if (defaultNode.equals(fromNode) || defaultNode.equals(toNode)) {
                    JOptionPane.showMessageDialog(tree, "不能移动默认节点！", "非法操作", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 同级移动
                Node parent = (Node) toNode.getParent();
                int index = parent.getIndex(toNode);
                parent.insert(fromNode, index);
            }
            if (fromDepth == 3) {
                Node parent = (Node) fromNode.getParent();
                String group = (String) parent.getUserObject();
                Repository repo = (Repository) fromNode.getUserObject();
                // 修改仓库节点的分组名
                repo.setGroup(group);
            }
            treeModel.reload();
            expandTree();
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
