package cn.gjr.task;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Repository;
import cn.gjr.utils.GitUtil;

import java.util.List;

/**
 * 任务类-分支
 *
 * @author GaoJunru
 */
public class BranchTask extends BaseTask {
    public BranchTask(Repository repository) {
        super(repository);
    }

    @Override
    public void run() {
        List<Branch> branches = GitUtil.getBranchList(repository);
        repository.setBranchList(branches);
        setStatus();
    }

    /**
     * 修改文件状态
     */
    private void setStatus() {
        String status = GitUtil.status(repository.getDir()).getMessage().trim();
        GitUtil.setStatus(repository, status);
    }
}
