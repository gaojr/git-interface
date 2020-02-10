package cn.gjr.task;

import cn.gjr.bean.Branch;
import cn.gjr.utils.GitUtil;
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
    }
}
