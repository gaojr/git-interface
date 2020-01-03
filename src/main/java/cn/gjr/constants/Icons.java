package cn.gjr.constants;

import cn.gjr.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

/**
 * 常量-图标
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Getter
public enum Icons {
    /**
     * 分支
     */
    BRANCH("branch"),
    /**
     * 仓库
     */
    REPOSITORY("repository");

    /**
     * 文件名
     */
    File value;

    Icons(String fileName) {
        URL path = this.getClass().getResource("/icon/" + fileName + ".png");
        File file = FileUtils.toFile(path);
        if (!FileUtil.isImage(file)) {
            file = null;
        }
        this.value = file;
    }

    @Override
    public String toString() {
        return value.getPath();
    }
}
