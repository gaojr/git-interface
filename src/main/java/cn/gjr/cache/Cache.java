package cn.gjr.cache;

import cn.gjr.bean.Repository;

import java.util.List;

/**
 * 缓存
 *
 * @author GaoJunru
 */
public final class Cache {
    private static List<Repository> repositoryList;

    private Cache() {
    }

    public static List<Repository> getRepositoryList() {
        return repositoryList;
    }

    public static void setRepositoryList(List<Repository> repositoryList) {
        Cache.repositoryList = repositoryList;
    }
}
