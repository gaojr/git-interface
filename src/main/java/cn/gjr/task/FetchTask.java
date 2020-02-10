package cn.gjr.task;

import cn.gjr.bean.Repository;
import cn.gjr.utils.GitUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务类-拉取
 *
 * @author GaoJunru
 */
@Slf4j
public class FetchTask extends BaseTask {
    public FetchTask(Repository repository) {
        super(repository);
    }

    @Override
    public void run() {
        GitUtil.fetch(repository.getDir());
    }
}
