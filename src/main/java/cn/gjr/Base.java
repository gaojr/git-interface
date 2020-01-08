package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Config;
import cn.gjr.bean.Repository;
import cn.gjr.utils.FileUtil;
import cn.gjr.utils.GitUtil;
import cn.gjr.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    @Getter
    @Setter
    private List<Repository> repositoryList;

    public static void main(String[] args) {
        if (!GitUtil.hasGit()) {
            log.error("未安装git!!");
            return;
        }
        new Base();
    }

    private Base() {
        // Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
        // For thread safety, this method should be invoked from the event-dispatching thread.
        javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    /**
     * 生成并显示界面
     */
    private void createAndShowGUI() {
        // 处理分支
        repositoryList = config2Repository(readConfig());
        generateRepositoryList(repositoryList);
        // 生成frame
        JFrame frame = new JFrame("git工具");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                writeConfig(repositoryList);
            }
        });
        // 生成panel
        JPanel panel = DynamicTreeDemo.createAndShowGUI(this);
        // panel放入frame
        frame.setContentPane(panel);
        // 调整大小
        frame.setPreferredSize(new Dimension(500, 400));
        // 显示
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * 写入配置
     */
    private void writeConfig(List<Repository> repositoryList) {
        List<Config> configList = repository2Config(repositoryList);
        // TODO 写入配置
    }

    /**
     * 处理配置列表
     *
     * @param repositoryList 仓库list
     * @return 配置list
     */
    private List<Config> repository2Config(List<Repository> repositoryList) {
        List<Config> list = new ArrayList<>(repositoryList.size());
        repositoryList.forEach(e -> {
            Config config = new Config(e);
            list.add(config);
        });
        return deduplicate(list);
    }

    /**
     * 读取配置
     *
     * @return 仓库list
     */
    private List<Config> readConfig() {
        InputStream inputStream = Base.class.getResourceAsStream("/config.json");
        try {
            String config = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            TypeToken<ArrayList<Config>> typeToken = new TypeToken<ArrayList<Config>>() {
            };
            List<Config> list = JsonUtil.string2Bean(config, typeToken);
            return deduplicate(list);
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
    private List<Repository> config2Repository(List<Config> configList) {
        List<Repository> list = new ArrayList<>(configList.size());
        configList.forEach(e -> {
            // 转为系统路径
            String path = FilenameUtils.separatorsToSystem(e.getPath());
            if (FileUtil.isDirectory(path) && GitUtil.isRepository(path)) {
                Repository repository = new Repository();
                repository.setName(e.getName());
                File dir = new File(path);
                repository.setDir(dir);
                repository.setPath(dir.getPath());
                list.add(repository);
            }
        });
        return deduplicate(list);
    }

    /**
     * 完善仓库列表
     * TODO 优化，提高速度
     *
     * @param repositoryList 仓库列表
     */
    private void generateRepositoryList(List<Repository> repositoryList) {
        repositoryList.forEach(r -> {
            List<Branch> branches = GitUtil.getBranchList(r.getDir());
            r.setBranchList(branches);
        });
    }

    /**
     * 去重
     *
     * @param list 列表
     * @return 去重后的列表
     */
    private static <T> List<T> deduplicate(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }
}
