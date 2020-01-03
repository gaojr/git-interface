package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Config;
import cn.gjr.bean.Repository;
import cn.gjr.cache.Cache;
import cn.gjr.constants.Commands;
import cn.gjr.constants.Titles;
import cn.gjr.utils.FileUtil;
import cn.gjr.utils.GitUtil;
import cn.gjr.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.gjr.constants.Commands.*;
import static cn.gjr.constants.Titles.*;

/**
 * 动态树 Demo
 *
 * @author GaoJunru
 */
@Slf4j
public class DynamicTreeDemo extends JPanel implements ActionListener {

    private DynamicTree treePanel;

    /**
     * 构造函数
     */
    private DynamicTreeDemo() {
        super(new BorderLayout());
        setOpaque(true);

        // Create the components.
        treePanel = new DynamicTree();
        createTree(treePanel);

        // Lay everything out.
        // 调整大小
        treePanel.setPreferredSize(new Dimension(300, 500));
        add(treePanel, BorderLayout.CENTER);

        createButtons();
    }

    /**
     * 生成并显示界面
     * For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // 生成frame
        JFrame frame = new JFrame("git工具");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 生成panel
        DynamicTreeDemo newContentPane = new DynamicTreeDemo();
        frame.setContentPane(newContentPane);

        // 显示
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (!GitUtil.hasGit()) {
            log.error("未安装git!!");
            return;
        }
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(DynamicTreeDemo::createAndShowGUI);
    }

    /**
     * 生成并添加按钮
     */
    private void createButtons() {
        // 新增按钮 = 新增仓库
        JButton addButton = createButton(TITLE_ADD, COMMAND_ADD);
        // 移除按钮 = 移除仓库
        JButton removeButton = createButton(TITLE_REMOVE, COMMAND_REMOVE);
        // 刷新按钮 = 拉取并更新所有分支状态
        JButton refreshButton = createButton(TITLE_REFRESH, COMMAND_REFRESH);

        // 添加到面板
        JPanel btnPanel = new JPanel(new GridLayout(0, 3));
        btnPanel.add(addButton);
        btnPanel.add(removeButton);
        btnPanel.add(refreshButton);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * 生成按钮
     *
     * @param title 标题
     * @param command 命令
     * @return 按钮
     */
    private JButton createButton(Titles title, Commands command) {
        JButton addButton = new JButton(title.getValue());
        addButton.setActionCommand(command.toString());
        addButton.addActionListener(this);
        return addButton;
    }

    /**
     * 生成树
     *
     * @param treePanel 面板
     */
    private void createTree(DynamicTree treePanel) {
        List<Repository> repositoryList = config2Repository(readConfig());
        generateRepositoryList(repositoryList);
        Cache.setRepositoryList(repositoryList);

        // 生成树
        for (Repository repository : repositoryList) {
            DefaultMutableTreeNode rNode = treePanel.addObject(null, repository, true);
            // TODO 生成树节点后面的按钮
            for (Branch branch : repository.getBranchList()) {
                DefaultMutableTreeNode bNode = treePanel.addObject(rNode, branch);
                // TODO 生成树节点后面的按钮
            }
        }
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
     * 处理仓库列表
     * TODO 过滤掉重复的路径、名称
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
                repository.setPath(path);
                repository.setDir(new File(path));
                list.add(repository);
            }
        });
        return list;
    }

    /**
     * 读取配置
     *
     * @return 仓库list
     */
    private List<Config> readConfig() {
        InputStream inputStream = this.getClass().getResourceAsStream("/config.json");
        try {
            String config = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            TypeToken<ArrayList<Config>> typeToken = new TypeToken<ArrayList<Config>>() {
            };
            return JsonUtil.string2Bean(config, typeToken);
        } catch (IOException e) {
            log.error("Read Config Error!", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Commands command = Commands.valueOf(event.getActionCommand());
        switch (command) {
            case COMMAND_ADD:
                treePanel.add();
                return;
            case COMMAND_REMOVE:
                treePanel.remove();
                return;
            case COMMAND_REFRESH:
                treePanel.refresh();
                return;
            default:
                log.error("Error Command!");
        }
    }
}
