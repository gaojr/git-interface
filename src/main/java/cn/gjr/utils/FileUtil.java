package cn.gjr.utils;

import java.io.File;

/**
 * 文件 util
 *
 * @author GaoJunru
 */
public final class FileUtil {
    private FileUtil() {
    }

    /**
     * 是否为存在的目录
     *
     * @param path 路径
     * @return {@code true} 是
     */
    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }
}
