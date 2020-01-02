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
    COMMAND_ADD("add"),
    /**
     * 获取
     */
    COMMAND_FETCH("fetch"),
    /**
     * 变基
     */
    COMMAND_REBASE("rebase"),
    /**
     * 刷新
     */
    COMMAND_REFRESH("refresh"),
    /**
     * 移除
     */
    COMMAND_REMOVE("remove");

    /**
     * 路径
     */
    String value;
}
