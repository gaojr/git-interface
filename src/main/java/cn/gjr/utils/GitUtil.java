package cn.gjr.utils;

import cn.gjr.bean.Branch;
import cn.gjr.bean.CommandResult;
import cn.gjr.bean.Repository;
import cn.gjr.task.BranchTask;
import cn.gjr.task.Pool;
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
        CommandResult result = CommandUtil.run(command, dir);
        logInfo(branchName, "checkout", result.isSuccess());
        return result;
    }

    /**
     * 查看分支状态
     *
     * @param repo 仓库
     * @return 命令运行结果
     */
    public static CommandResult branch(Repository repo) {
        String command = "git branch --format=\"{" +
                "\\\"name\\\":\\\"%(refname:short)\\\"," +
                "\\\"upstream\\\":\\\"%(upstream:short)\\\"," +
                "\\\"isCurrent\\\":\"%(if)%(HEAD)%(then)true%(else)false%(end)\"," +
                "\\\"track\\\":\\\"%(upstream:track,nobracket)\\\"}%0a\"";
        CommandResult result = CommandUtil.run(command, repo.getDir());
        logInfo(repo.getName(), "branch", result.isSuccess());
        return result;
    }

    /**
     * 拉取
     *
     * @param repo 仓库
     * @return 命令运行结果
     */
    public static CommandResult fetch(Repository repo) {
        String command = "git fetch --all --prune";
        CommandResult result = CommandUtil.run(command, repo.getDir());
        logInfo(repo.getName(), "fetch", result.isSuccess());
        return result;
    }

    /**
     * 变基
     *
     * @param branch 分支
     * @return 命令运行结果
     */
    public static CommandResult rebase(Branch branch) {
        File dir = branch.getRepository().getDir();
        CommandResult result = branch.isCurrent() ? rebase(dir) : rebase(dir, branch.getName());
        logInfo(branch.getName(), "rebase", result.isSuccess());
        return result;
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
     * 是否为分支对象
     *
     * @param obj 对象
     * @return {@code true} 是
     */
    public static boolean isBranch(Object obj) {
        return obj instanceof Branch;
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
     * @param repository 仓库
     * @return 分支列表
     */
    public static List<Branch> getBranchList(Repository repository) {
        String message = branch(repository).getMessage().trim();
        String[] branches = message.split("\n\n");
        List<Branch> branchList = new ArrayList<>(branches.length);
        for (String branchStr : branches) {
            JsonObject json = JsonUtil.string2Json(branchStr);
            Branch br = new Branch();
            br.setRepository(repository);
            br.setCurrent(json.get("isCurrent").getAsBoolean());
            br.setName(json.get("name").getAsString());
            br.setUpstream(json.get("upstream").getAsString());
            String track = json.get("track").getAsString();
            branchTrack(br, track);
            branchList.add(br);
        }
        String status = status(repository.getDir()).getMessage().trim();
        setStatus(repository, status);
        return branchList;
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
     * 填入分支状态
     *
     * @param repo 分支
     * @param statusStr 分支状态
     */
    private static void setStatus(Repository repo, String statusStr) {
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
        repo.setAdd(add);
        repo.setDelete(delete);
        repo.setModify(modify);
        repo.setCanRebase(add < 1 && modify < 1 && delete < 1);
    }

    /**
     * 完善仓库列表
     */
    public static void generateRepositoryList(List<Repository> repositoryList) {
        Pool pool = new Pool(repositoryList.size());
        repositoryList.forEach(e -> {
            BranchTask task = new BranchTask(e);
            pool.add(task);
        });
        pool.run();
    }

    /**
     * 打印日志
     *
     * @param name 对象名
     * @param command 命令
     * @param isSuccess 是否成功
     */
    private static void logInfo(String name, String command, boolean isSuccess) {
        log.info("{} [{}] {}", name, command, isSuccess ? "success" : "failed");
    }
}
