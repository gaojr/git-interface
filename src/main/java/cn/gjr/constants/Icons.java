package cn.gjr.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 常量-图标
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Getter
public enum Icons {
    /**
     * TODO 获取
     */
    ICON_FETCH("fetch"),
    /**
     * TODO 变基
     */
    ICON_REBASE("rebase");

    /**
     * 路径
     */
    String value;

    @Override
    public String toString() {
        return value;
    }
}
