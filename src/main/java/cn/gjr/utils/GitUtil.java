package cn.gjr.utils;

import cn.gjr.bean.Branch;
import cn.gjr.bean.CommandResult;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * git util
 *
 * @author GaoJunru
 */
@Slf4j
public final class GitUtil {
    private GitUtil() {
    }

    /**
     * 查看分支状态
     *
     * @param dir 目录
     * @return 命令运行结果
     */
    public static CommandResult branch(File dir) {
        String command = "git branch --format=\"{\\\"name\\\":\\\"%(refname:short)\\\",\\\"upstream\\\":\\\"%(upstream:short)\\\",\\\"isCurrent\\\":\"%(if)%(HEAD)%(then)true%(else)false%(end)\",\\\"track\\\":\\\"%(upstream:track,nobracket)\\\"}%0a\"";
        return CommandUtil.run(command, dir);
    }

    /**
     * 拉取
     *
     * @param dir 目录
     * @return 命令运行结果
     */
    public static CommandResult fetch(File dir) {
        String command = "git fetch --all --prune";
        return CommandUtil.run(command, dir);
    }

    /**
     * 变基
     *
     * @param dir 目录
     * @return 命令运行结果
     */
    public static CommandResult rebase(File dir) {
        String command = "git rebase";
        return CommandUtil.run(command, dir);
    }

    /**
     * 变基
     *
     * @param dir 目录
     * @param branchName 分支名
     * @return 命令运行结果
     */
    public static CommandResult rebase(File dir, String branchName) {
        String command = "git checkout " + branchName + " | git rebase | git checkout -";
        return CommandUtil.run(command, dir);
    }

    /**
     * 状态
     *
     * @param dir 目录
     * @return 命令运行结果
     */
    public static CommandResult status(File dir) {
        String command = "git status --short";
        return CommandUtil.run(command, dir);
    }

    /**
     * 状态
     *
     * @param dir 目录
     * @param branchName 分支名
     * @return 命令运行结果
     */
    public static CommandResult status(File dir, String branchName) {
        String command = "git checkout " + branchName;
        CommandUtil.run(command, dir);
        return status(dir);
    }

    /**
     * 判断是否git仓库
     *
     * @param path 路径
     * @return {@code true} 是git仓库
     */
    public static boolean isRepository(String path) {
        return isRepository(new File(path));
    }

    /**
     * 判断是否git仓库
     *
     * @param dir 目录
     * @return {@code true} 是git仓库
     */
    public static boolean isRepository(File dir) {
        return status(dir).isSuccess();
    }

    /**
     * 判断是否安装git
     *
     * @return {@code true} 已安装
     */
    public static boolean hasGit() {
        String command = "git --version";
        CommandResult result = CommandUtil.run(command);
        return result.isSuccess();
    }

    /**
     * 根据目录获取分支列表
     *
     * @param dir 目录
     * @return 分支列表
     */
    public static List<Branch> getBranchList(File dir) {
        String message = branch(dir).getMessage().trim();
        String[] branches = message.split("\n\n");
        List<Branch> branchList = new ArrayList<>(branches.length);
        for (String branchStr : branches) {
            JsonObject json = JsonUtil.string2Json(branchStr);
            Branch br = new Branch();
            br.setDir(dir);
            br.setCurrent(json.get("isCurrent").getAsBoolean());
            br.setName(json.get("name").getAsString());
            br.setUpstream(json.get("upstream").getAsString());
            String track = json.get("track").getAsString();
            branchTrack(br, track);
            String status = status(dir, br.getName()).getMessage().trim();
            branchStatus(br, status);
            branchList.add(br);
        }
        return branchList;
    }

    /**
     * 填入分支状态
     *
     * @param br 分支
     * @param statusStr 分支状态
     */
    private static void branchStatus(Branch br, String statusStr) {
        if (StringUtils.isBlank(statusStr)) {
            return;
        }
        String[] statuses = statusStr.split("\n");
        int add = 0;
        int delete = 0;
        int modify = 0;
        for (String status : statuses) {
            status = status.trim();
            if (StringUtils.startsWith(status, "D ")) {
                delete++;
            } else if (StringUtils.startsWith(status, "M ") || StringUtils.startsWith(status, "R ")) {
                modify++;
            } else if (StringUtils.startsWith(status, "?? ") || StringUtils.startsWith(status, "A ")) {
                add++;
            }
            // TODO 还有一些奇怪的状态……例如: copied {@link https://git-scm.com/docs/git-status#_changed_tracked_entries}
        }
        br.setAdd(add);
        br.setDelete(delete);
        br.setModify(modify);
    }

    /**
     * 填入修改状态
     *
     * @param br 分支
     * @param track 修改状态
     */
    private static void branchTrack(Branch br, String track) {
        if (StringUtils.isBlank(track)) {
            return;
        }
        String[] tracks = track.split(", ");
        for (String t : tracks) {
            if (StringUtils.startsWith(t, "ahead")) {
                String str = StringUtils.substringAfter(t, "ahead ");
                br.setAhead(Integer.parseInt(str));
            } else if (StringUtils.startsWith(t, "behind")) {
                String str = StringUtils.substringAfter(t, "behind ");
                br.setBehind(Integer.parseInt(str));
            }
        }
    }
}
