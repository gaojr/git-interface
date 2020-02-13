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
}
