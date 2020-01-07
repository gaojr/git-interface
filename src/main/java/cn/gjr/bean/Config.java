package cn.gjr.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 配置
 *
 * @author GaoJunru
 */
@Data
@NoArgsConstructor
public class Config {
    /**
     * 名称
     */
    private String name;
    /**
     * 路径
     */
    private String path;

    public Config(Repository e) {
        name = e.getName();
        path = e.getPath();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Config that = (Config) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
