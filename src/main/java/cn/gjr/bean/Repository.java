package cn.gjr.bean;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * 仓库
 *
 * @author GaoJunru
 */
@Data
public class Repository {
    /**
     * 名称
     */
    @Expose
    private String name;
    /**
     * 路径
     */
    @Expose
    private String path;
    /**
     * 目录
     */
    @Expose(serialize = false)
    private File dir;
    /**
     * 分支
     */
    @Expose
    private List<Branch> branchList;

    @Override
    public String toString() {
        return String.format("%s (%s)", name, path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Repository that = (Repository) o;
        return Objects.equals(name, that.name) || Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        JsonObject json = new JsonObject();
        json.addProperty(name, path);
        return Objects.hash(json);
    }
}
