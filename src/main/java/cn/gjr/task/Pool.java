package cn.gjr.task;

import lombok.SneakyThrows;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池类
 *
 * @author GaoJunru
 */
public class Pool {
    /**
     * 线程池
     */
    ExecutorService pool;

    /**
     * 构造函数
     *
     * @param size 大小
     */
    public Pool(int size) {
        size = Math.max(size, 1);
        pool = new ThreadPoolExecutor(size, size, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(512), // 使用有界队列，避免OOM
                new ThreadPoolExecutor.DiscardPolicy() // 什么也不做，直接忽略
        );
    }

    /**
     * 添加任务
     *
     * @param task 任务
     */
    public void add(BaseTask task) {
        pool.execute(task);
    }

    /**
     * 运行任务
     */
    @SneakyThrows(InterruptedException.class)
    public void run() {
        pool.shutdown();
        while (!pool.isTerminated()) {
            Thread.sleep(200);
        }
    }
}
