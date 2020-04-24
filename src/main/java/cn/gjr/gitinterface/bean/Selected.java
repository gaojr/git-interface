package cn.gjr.gitinterface.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 已选择的对象
 *
 * @author GaoJunru
 */
@Data
@NoArgsConstructor
public class Selected {
    /**
     * 仓库列表
     */
    private Set<Repository> repositories;
    /**
     * 分支列表
     */
    private Set<Branch> branches;
    /**
     * 节点-仓库
     */
    private Set<Node> repoNodes = new HashSet<>();
    /**
     * 节点-分支
     */
    private Set<Node> branchNodes = new HashSet<>();

    /**
     * 添加节点
     *
     * @param node 节点
     */
    public void addNode(Node node) {
        repoNodes.addAll(node.getRepositoryNode());
        branchNodes.addAll(node.getBranchNode());
    }
}
