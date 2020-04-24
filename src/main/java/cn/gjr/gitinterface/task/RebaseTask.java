package cn.gjr.gitinterface.task;

import cn.gjr.gitinterface.bean.Branch;
import cn.gjr.gitinterface.utils.GitUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务类-变基
 *
 * @author GaoJunru
 */
@Slf4j
public class RebaseTask extends BaseTask {
    /**
     * 分支
     */
    Branch branch;

    public RebaseTask(Branch branch) {
        this.branch = branch;
    }

    @Override
    public void run() {
        GitUtil.rebase(branch);
        // TODO rebase失败后的处理
    }
}
