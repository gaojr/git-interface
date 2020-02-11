package cn.gjr.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 常量-命令
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Getter
public enum Commands {
    /**
     * 获取
     */
    FETCH("fetch"),
    /**
     * 增加-分组
     */
    GROUP("group"),
    /**
     * 变基
     */
    REBASE("rebase"),
    /**
     * 移除
     */
    REMOVE("remove"),
    /**
     * 增加-仓库
     */
    REPO("repo"),
    /**
     * 保存
     */
    SAVE("save");

    /**
     * 命令
     */
    String value;
}
