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
     * 增加
     */
    ADD("add"),
    /**
     * 获取
     */
    FETCH("fetch"),
    /**
     * 变基
     */
    REBASE("rebase"),
    /**
     * 移除
     */
    REMOVE("remove");

    /**
     * 命令
     */
    String value;
}
