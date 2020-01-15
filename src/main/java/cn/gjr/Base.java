package cn.gjr;

import cn.gjr.bean.Repository;
import cn.gjr.utils.FileUtil;
import cn.gjr.utils.GitUtil;
import cn.gjr.utils.JsonUtil;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础方法
 *
 * @author GaoJunru
 */
@Slf4j
class Base {
    /**
     * 配置文件
     */
    private File configFile;

    /**
     * 仓库列表
     */
    @Getter
    @Setter
    private List<Repository> repositoryList;

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
            return Collections.emptyList();
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 生成并显示界面
     */
    private void createAndShowGUI() {
        if (initConfigFile()) {
            return;
        }
        // 处理分支
        repositoryList = readConfig();
        GitUtil.generateRepositoryList(repositoryList);
        // 生成frame
        JFrame frame = new JFrame("git工具");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                writeConfig();
            }
        });
        // 生成panel
        JPanel panel = DynamicTreeDemo.createAndShowGUI(this);
        // panel放入frame
        frame.setContentPane(panel);
        // 调整大小
        frame.setPreferredSize(new Dimension(450, 400));
        // 显示
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * 初始化配置文件
     *
     * @return {@code true} 初始化失败
     */
    private boolean initConfigFile() {
        try {
            URL url = Base.class.getResource("/config.json");
            configFile = new File(url.toURI());
        } catch (URISyntaxException e) {
            log.error("获取配置文件失败！", e);
            return true;
        }
        return false;
    }

    /**
     * 写入配置
     */
    private void writeConfig() {
        TypeToken<List<Repository>> typeToken = new TypeToken<List<Repository>>() {
        };
        JsonArray array = JsonUtil.list2Array(repositoryList, typeToken);
        String output = JsonUtil.array2String(array);
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            IOUtils.write(output, outputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Write Config Error!", e);
        }
    }

    /**
     * 读取配置
     *
     * @return 仓库list
     */
    private List<Repository> readConfig() {
        try (InputStream inputStream = new FileInputStream(configFile)) {
            String config = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            TypeToken<ArrayList<Repository>> typeToken = new TypeToken<ArrayList<Repository>>() {
            };
            List<Repository> list = JsonUtil.string2Bean(config, typeToken);
            return config2Repository(list);
        } catch (IOException e) {
            log.error("Read Config Error!", e);
        }
        return Collections.emptyList();
    }

    /**
     * 处理仓库列表
     *
     * @param configList 仓库list
     * @return 校正后的仓库list
     */
    private List<Repository> config2Repository(List<Repository> configList) {
        List<Repository> list = new ArrayList<>(configList.size());
        configList.stream().filter(e -> StringUtils.isNoneBlank(e.getName(), e.getPath())).forEach(e -> {
            // 转为系统路径
            String path = FilenameUtils.separatorsToSystem(e.getPath());
            if (FileUtil.isDirectory(path) && GitUtil.isRepository(path)) {
                File dir = new File(path);
                e.setDir(dir);
                e.setPath(dir.getPath());
                list.add(e);
            }
        });
        return deduplicate(list);
    }
}
