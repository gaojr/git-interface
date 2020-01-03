package cn.gjr.utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

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

    /**
     * 是否为图片(bmp/gif/jpg/png)
     *
     * @param file 文件
     * @return {@code true} 是
     */
    public static boolean isImage(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (IOException ignored) {
        }
        return false;
    }
}
