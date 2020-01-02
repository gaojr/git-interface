package cn.gjr.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 命令运行结果
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Data
public class CommandResult {
    /**
     * 命令行
     */
    private String command;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 信息
     */
    private String message;
}
