package cn.gjr.bean;

import lombok.Getter;
import lombok.Setter;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * 节点
 *
 * @author GaoJunru
 */
public class Node extends DefaultMutableTreeNode {
    /**
     * 节点类型-根节点
     */
    public static final int TYPE_ROOT = 0;
    /**
     * 节点类型-分组
     */
    public static final int TYPE_GROUP = 1;
    /**
     * 节点类型-仓库
     */
    public static final int TYPE_REPO = 2;
    /**
     * 节点类型-分支
     */
    public static final int TYPE_BRANCH = 3;
    /**
     * 节点类型
     */
    @Getter
    @Setter
    private int type;

    public Node(Object userObject) {
        super(userObject);
        if (userObject instanceof Branch) {
            type = TYPE_BRANCH;
        } else if (userObject instanceof Repository) {
            type = TYPE_REPO;
        } else {
            type = TYPE_GROUP;
        }
    }

    /**
     * 获取节点下的分组
     *
     * @return 分组list
     */
    public List<String> getGroupList() {
        List<String> list = new ArrayList<>();
        switch (type) {
            case TYPE_ROOT:
                for (int i = 0; i < getChildCount(); i++) {
                    Node node = (Node) getChildAt(i);
                    list.addAll(node.getGroupList());
                }
                break;
            case TYPE_GROUP:
                list.add((String) getUserObject());
            case TYPE_REPO:
            case TYPE_BRANCH:
            default:
                break;
        }
        return list;
    }

    /**
     * 获取节点下的仓库
     *
     * @return 仓库list
     */
    public List<Repository> getRepositoryList() {
        List<Repository> list = new ArrayList<>();
        switch (type) {
            case TYPE_ROOT:
            case TYPE_GROUP:
                for (int i = 0; i < getChildCount(); i++) {
                    Node node = (Node) getChildAt(i);
                    list.addAll(node.getRepositoryList());
                }
                break;
            case TYPE_REPO:
                list.add((Repository) getUserObject());
                break;
            case TYPE_BRANCH:
                Node parent = (Node) getParent();
                list.add((Repository) parent.getUserObject());
                break;
            default:
                break;
        }
        return list;
    }

    /**
     * 获取节点下的分支
     *
     * @return 分支list
     */
    public List<Branch> getBranchList() {
        List<Branch> list = new ArrayList<>();
        switch (type) {
            case TYPE_ROOT:
            case TYPE_GROUP:
            case TYPE_REPO:
                for (int i = 0; i < getChildCount(); i++) {
                    Node node = (Node) getChildAt(i);
                    list.addAll(node.getBranchList());
                }
                break;
            case TYPE_BRANCH:
                list.add((Branch) getUserObject());
            default:
                break;
        }
        return list;
    }

    /**
     * 获取节点所属的仓库节点
     *
     * @return 节点
     */
    public List<Node> getRepositoryNode() {
        List<Node> list = new ArrayList<>();
        switch (type) {
            case TYPE_ROOT:
            case TYPE_GROUP:
                for (int i = 0; i < getChildCount(); i++) {
                    Node node = (Node) getChildAt(i);
                    list.addAll(node.getRepositoryNode());
                }
                break;
            case TYPE_REPO:
                list.add(this);
                break;
            case TYPE_BRANCH:
                list.add((Node) getParent());
                break;
            default:
                break;
        }
        return list;
    }

    /**
     * 获取节点所属的分支节点
     *
     * @return 节点
     */
    public List<Node> getBranchNode() {
        List<Node> list = new ArrayList<>();
        switch (type) {
            case TYPE_ROOT:
            case TYPE_GROUP:
            case TYPE_REPO:
                for (int i = 0; i < getChildCount(); i++) {
                    Node node = (Node) getChildAt(i);
                    list.addAll(node.getBranchNode());
                }
                break;
            case TYPE_BRANCH:
                list.add(this);
                break;
            default:
                break;
        }
        return list;
    }
}
