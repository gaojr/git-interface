package cn.gjr.task;

import cn.gjr.bean.Repository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 任务类
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseTask implements Runnable {
    /**
     * 仓库
     */
    Repository repository;
}
