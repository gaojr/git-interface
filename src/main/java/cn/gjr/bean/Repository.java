package cn.gjr.bean;

import com.google.gson.JsonObject;
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
public class Repository extends Config {
    /**
     * 目录
     */
    private File dir;
    /**
     * 分支
     */
    private List<Branch> branchList;

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), dir.getPath());
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
        return Objects.equals(getName(), that.getName()) && Objects.equals(dir.getPath(), that.dir.getPath());
    }

    @Override
    public int hashCode() {
        JsonObject json = new JsonObject();
        json.addProperty(getName(), dir.getPath());
        return Objects.hash(json);
    }
}
