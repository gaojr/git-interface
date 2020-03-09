package cn.gjr.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件 util
 *
 * @author GaoJunru
 */
@Slf4j
public final class FileUtil {
    private FileUtil() {
    }

    /**
     * 是否为文件
     *
     * @param file 文件
     * @return {@code true} 存在且为文件
     */
    public static boolean isFile(File file) {
        return file != null && file.exists() && file.isFile();
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
     * 返回图片(bmp/gif/jpg/png)
     *
     * @param is 文件
     * @return 图片
     */
    public static BufferedImage getImage(InputStream is) {
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            log.error("图片读取失败", e);
        }
        return null;
    }
}
