package cn.gjr.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 已选择的对象
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Data
public class Selected {
    /**
     * 仓库列表
     */
    private List<Repository> repositoryList;
    /**
     * 分支列表
     */
    private List<Branch> branchList;

    /**
     * 获取所有仓库
     *
     * @return 仓库set
     */
    public Set<Repository> getRepositorySet() {
        Set<Repository> set = new HashSet<>(repositoryList);
        branchList.forEach(e -> set.add(e.getRepository()));
        return set;
    }

    /**
     * 获取所有分支
     *
     * @return 分支set
     */
    public Set<Branch> getBranchSet() {
        Set<Branch> set = new HashSet<>(branchList);
        repositoryList.forEach(e -> set.addAll(e.getBranchList()));
        return set;
    }
}
