package cn.gjr.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 常量-标题
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Getter
public enum Titles {
    /**
     * 增加-分组
     */
    ADD_GROUP("+分组"),
    /**
     * 增加-仓库
     */
    ADD_REPO("+仓库"),
    /**
     * 获取
     */
    FETCH("Fetch"),
    /**
     * 变基
     */
    REBASE("Rebase"),
    /**
     * 移除
     */
    REMOVE("-移除"),
    /**
     * 保存
     */
    SAVE("保存");

    /**
     * 标题
     */
    String value;
}
