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
     * 增加
     */
    ADD("Add"),
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
    REMOVE("Remove"),
    /**
     * 保存
     */
    SAVE("Save");

    /**
     * 标题
     */
    String value;
}
