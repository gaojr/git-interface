package cn.gjr.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

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
    private Set<Repository> repositories;
    /**
     * 分支列表
     */
    private Set<Branch> branches;
}
