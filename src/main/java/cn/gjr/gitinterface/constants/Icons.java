package cn.gjr.gitinterface.constants;

import cn.gjr.gitinterface.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * 常量-图标
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Getter
public enum Icons {
    /**
     * 分组
     */
    GROUP("group"),
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
    BufferedImage value;

    Icons(String fileName) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("icon/" + fileName + ".png");
        this.value = FileUtil.getImage(is);
    }
}
