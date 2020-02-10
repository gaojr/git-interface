package cn.gjr.task;

import lombok.SneakyThrows;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 *
 * @author GaoJunru
 */
public class Pool {
    ExecutorService pool;

    public Pool(int size) {
        pool = new ThreadPoolExecutor(size, size, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(512), // 使用有界队列，避免OOM
                new ThreadPoolExecutor.DiscardPolicy() // 什么也不做，直接忽略
        );
    }

    public void add(BaseTask task) {
        pool.execute(task);
    }

    @SneakyThrows(InterruptedException.class)
    public void run() {
        pool.shutdown();
        while (!pool.isTerminated()) {
            Thread.sleep(200);
        }
    }
}
