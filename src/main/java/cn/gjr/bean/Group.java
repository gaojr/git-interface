package cn.gjr.bean;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.Objects;

/**
 * 分组
 *
 * @author GaoJunru
 */
@Data
public class Group {
    /**
     * 名称
     */
    @Expose
    private String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Group that = (Group) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
