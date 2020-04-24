package cn.gjr.gitinterface.utils;

import cn.gjr.gitinterface.bean.CommandResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 命令 util
 *
 * @author GaoJunru
 */
@Slf4j
public final class CommandUtil {
    private CommandUtil() {
    }

    /**
     * 运行命令
     *
     * @param command 命令
     * @return 命令运行结果
     */
    public static CommandResult run(String command) {
        return run(command, null);
    }

    /**
     * 运行命令
     *
     * @param command 命令
     * @param dir 目录
     * @return 命令运行结果
     */
    public static CommandResult run(String command, File dir) {
        String errorMsg;
        try {
            Process process = Runtime.getRuntime().exec(command, null, dir);
            String msg = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            errorMsg = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            log.info("\n{}[{}]{}\n*---*\n{}\n*---*\n{}\n*****", dir, command, process.waitFor(), msg, errorMsg);
            if (StringUtils.isEmpty(errorMsg)) {
                return success(command, msg);
            }
        } catch (IOException e) {
            errorMsg = e.getMessage();
        } catch (InterruptedException e) {
            errorMsg = e.getMessage();
            Thread.currentThread().interrupt();
        }
        log.error("command [{}] failed! error message: {}", command, errorMsg);
        return fail(command, errorMsg);
    }

    /**
     * 命令运行结果-成功
     *
     * @param command 命令
     * @param message 信息
     * @return 命令运行结果
     */
    private static CommandResult success(String command, String message) {
        return new CommandResult(command, true, message);
    }

    /**
     * 命令运行结果-失败
     *
     * @param command 命令
     * @param message 信息
     * @return 命令运行结果
     */
    private static CommandResult fail(String command, String message) {
        return new CommandResult(command, false, message);
    }
}
