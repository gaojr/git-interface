package cn.gjr.gitinterface;

import cn.gjr.gitinterface.bean.Repository;
import cn.gjr.gitinterface.frame.DynamicTreeDemo;
import cn.gjr.gitinterface.utils.FileUtil;
import cn.gjr.gitinterface.utils.GitUtil;
import cn.gjr.gitinterface.utils.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础方法
 *
 * @author GaoJunru
 */
@Slf4j
public class Base {
    /**
     * 类型-分组
     */
    TypeToken<List<String>> groupToken = new TypeToken<List<String>>() {
    };
    /**
     * 类型-仓库
     */
    TypeToken<List<Repository>> repoToken = new TypeToken<List<Repository>>() {
    };
    /**
     * 缓存文件路径-分组
     */
    private static String groupFilePath = "group.json";
    /**
     * 缓存文件路径-仓库
     */
    private static String repositoryFilePath = "repository.json";
    /**
     * 缓存文件-分组
     */
    private File groupFile;
    /**
     * 缓存文件-仓库
     */
    private File repositoryFile;
    /**
     * 分组列表
     */
    @Getter
    @Setter
    private List<String> groups;
    /**
     * 仓库列表
     */
    @Getter
    @Setter
    private List<Repository> repositories;
    /**
     * 面板
     */
    private DynamicTreeDemo demo;

    private Base() {
        // Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
        // For thread safety, this method should be invoked from the event-dispatching thread.
        javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public static void main(String[] args) {
        if (!GitUtil.hasGit()) {
            log.error("未安装git!!");
            return;
        }
        if (args != null && args.length > 1) {
            repositoryFilePath = args[0];
            groupFilePath = args[1];
        }
        new Base();
    }

    /**
     * 去重
     *
     * @param list 列表
     * @return 去重后的列表
     */
    private static <T> List<T> deduplicate(List<T> list) {
        if (ObjectUtils.isEmpty(list)) {
            return new ArrayList<>(10);
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 生成框架
     *
     * @param title 标题
     * @param panel 面板
     * @param width 宽
     * @param height 高
     * @return 框架
     */
    public static JFrame createFrame(String title, JPanel panel, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setTitle(title);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(width, height));
        // panel放入frame
        frame.setContentPane(panel);
        // 显示
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    /**
     * 生成并显示界面
     */
    private void createAndShowGUI() {
        if (!initConfigFile()) {
            log.error("获取配置文件失败！");
            return;
        }
        // 处理分支
        readConfig();
        // 生成panel
        demo = DynamicTreeDemo.createAndShowGUI(repositories, groups);
        JFrame frame = createFrame("git工具", demo, 450, 400);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                writeConfig();
            }
        });
    }

    /**
     * 初始化配置文件
     *
     * @return {@code true} 初始化失败
     */
    private boolean initConfigFile() {
        repositoryFile = new File(repositoryFilePath);
        groupFile = new File(groupFilePath);
        return FileUtil.isFile(repositoryFile) && FileUtil.isFile(groupFile);
    }

    /**
     * 写入配置
     */
    private void writeConfig() {
        // 仓库
        write(repoToken, demo.getRepositories(), repositoryFile);
        // 分组
        write(groupToken, demo.getGroups(), groupFile);
    }

    /**
     * 写入配置
     *
     * @param token 对象类型
     * @param data 被写入的数据
     * @param file 配置文件
     * @param <T> 类型
     */
    private <T> void write(TypeToken<List<T>> token, List<T> data, File file) {
        JsonArray array = JsonUtil.list2Array(data, token);
        String output = JsonUtil.array2String(array);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.write(output, outputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Write Config Error! ", e);
        }
    }

    /**
     * 读取配置
     */
    private void readConfig() {
        // 仓库
        repositories = readConfig(repoToken, repositoryFile);
        config2Repository(repositories);
        // 分组
        groups = readConfig(groupToken, groupFile);
    }

    /**
     * 读取配置
     *
     * @param token 对象类型
     * @param file 配置文件
     * @param <T> 类型
     * @return 配置信息
     */
    private <T> List<T> readConfig(TypeToken<List<T>> token, File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            String config = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            List<T> list = JsonUtil.string2Bean(config, token);
            return deduplicate(list);
        } catch (IOException e) {
            log.error("Read Config Error!", e);
        }
        return new ArrayList<>(10);
    }

    /**
     * 处理仓库列表
     *
     * @param configList 仓库list
     */
    private void config2Repository(List<Repository> configList) {
        configList.stream().filter(e -> StringUtils.isNoneBlank(e.getName(), e.getPath())).forEach(e -> {
            // 转为系统路径
            String path = FilenameUtils.separatorsToSystem(e.getPath());
            if (FileUtil.isDirectory(path) && GitUtil.isRepository(path)) {
                File dir = new File(path);
                e.setDir(dir);
                e.setPath(dir.getPath());
                e.getBranchList().forEach(b -> b.setRepository(e));
            }
        });
    }
}
