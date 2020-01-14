package cn.gjr.utils;

import cn.gjr.bean.Branch;
import cn.gjr.bean.CommandResult;
import cn.gjr.bean.Repository;
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
     * 切回上一个分支
     *
     * @param dir 目录
     * @return 命令运行结果
     */
    public static CommandResult checkoutBack(File dir) {
        return checkout(dir, "-");
    }

    /**
     * 切换分支
     *
     * @param dir 目录
     * @param branchName 分支名
     * @return 命令运行结果
     */
    public static CommandResult checkout(File dir, String branchName) {
        String command = "git checkout " + branchName;
        return CommandUtil.run(command, dir);
    }

    /**
     * 查看分支状态
     *
     * @param dir 目录
     * @return 命令运行结果
     */
    public static CommandResult branch(File dir) {
        String command = "git branch --format=\"{" +
                "\\\"name\\\":\\\"%(refname:short)\\\"," +
                "\\\"upstream\\\":\\\"%(upstream:short)\\\"," +
                "\\\"isCurrent\\\":\"%(if)%(HEAD)%(then)true%(else)false%(end)\"," +
                "\\\"track\\\":\\\"%(upstream:track,nobracket)\\\"}%0a\"";
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
     * @param branch 分支
     * @return 命令运行结果
     */
    public static CommandResult rebase(Branch branch) {
        if (branch.isCurrent()) {
            return rebase(branch.getDir());
        }
        return rebase(branch.getDir(), branch.getName());
    }

    /**
     * 变基
     *
     * @param dir 目录
     * @param branchName 分支名
     * @return 命令运行结果
     */
    private static CommandResult rebase(File dir, String branchName) {
        CommandResult result = checkout(dir, branchName);
        if (!result.isSuccess()) {
            return result;
        }
        result = rebase(dir);
        checkoutBack(dir);
        return result;
    }

    /**
     * 变基
     *
     * @param dir 目录
     * @return 命令运行结果
     */
    private static CommandResult rebase(File dir) {
        String command = "git rebase";
        return CommandUtil.run(command, dir);
    }

    /**
     * 状态
     *
     * @param branch 分支
     * @return 命令运行结果
     */
    public static CommandResult status(Branch branch) {
        File dir = branch.getDir();
        if (branch.isCurrent()) {
            return status(dir);
        }
        CommandResult result = checkout(dir, branch.getName());
        if (!result.isSuccess()) {
            return result;
        }
        result = status(dir);
        checkoutBack(dir);
        return result;
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
     * 是否为仓库对象
     *
     * @param obj 对象
     * @return {@code true} 是
     */
    public static boolean isRepository(Object obj) {
        return obj instanceof Repository;
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
            String status = status(br).getMessage().trim();
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
            if (StringUtils.startsWithAny(status, "D", "UD", "UU")) {
                delete++;
            } else if (StringUtils.startsWithAny(status, "M", "R")) {
                modify++;
            } else if (StringUtils.startsWithAny(status, "??", "A", "UA")) {
                add++;
            }
            // TODO 还有一些奇怪的状态……例如: copied {@link https://git-scm.com/docs/git-status#_changed_tracked_entries}
        }
        br.setAdd(add);
        br.setDelete(delete);
        br.setModify(modify);
        br.setCanRebase(add < 1 && modify < 1 && delete < 1);
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

    /**
     * 是否为分支对象
     *
     * @param obj 对象
     * @return {@code true} 是
     */
    public static boolean isBranch(Object obj) {
        return obj instanceof Branch;
    }

    /**
     * 完善仓库列表
     * TODO 优化，提高速度
     */
    public static void generateRepositoryList(List<Repository> repositoryList) {
        repositoryList.forEach(e -> {
            List<Branch> branches = GitUtil.getBranchList(e.getDir());
            e.setBranchList(branches);
        });
    }
}
