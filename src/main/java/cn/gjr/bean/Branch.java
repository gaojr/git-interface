package cn.gjr.bean;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.Objects;

/**
 * 分支
 *
 * @author GaoJunru
 */
@Data
public class Branch {
    /**
     * 仓库
     */
    @Expose(serialize = false)
    private Repository repository;
    /**
     * 名称
     */
    @Expose
    private String name;
    /**
     * 远端
     */
    @Expose
    private String upstream;
    /**
     * 是否当前分支
     */
    @Expose
    private boolean isCurrent = false;
    /**
     * 能否直接变基
     */
    @Expose
    private boolean canRebase = true;
    /**
     * 领先提交数量
     */
    @Expose
    private int ahead = 0;
    /**
     * 落后提交数量
     */
    @Expose
    private int behind = 0;
    /**
     * 新增文件数量
     */
    @Expose
    private int add = 0;
    /**
     * 修改文件数量
     */
    @Expose
    private int modify = 0;
    /**
     * 删除文件数量
     */
    @Expose
    private int delete = 0;

    @Override
    public String toString() {
        return String.format("%s%s[%s] ↑%d↓%d+%d~%d-%d", isCurrent ? "*" : " ", name, upstream, ahead, behind, add, modify, delete);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Branch that = (Branch) o;
        return Objects.equals(repository, that.repository) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository, name);
    }
}
