package cn.gjr.task;

import cn.gjr.bean.Repository;

/**
 * 任务类
 *
 * @author GaoJunru
 */
public abstract class BaseTask implements Runnable {
    Repository repository;

    public BaseTask() {
    }

    public BaseTask(Repository repository) {
        this.repository = repository;
    }
}
