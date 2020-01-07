package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Config;
import cn.gjr.bean.Repository;
import cn.gjr.constants.Commands;
import cn.gjr.constants.Icons;
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
import javax.swing.tree.DefaultTreeCellRenderer;
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
import java.util.stream.Collectors;

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

        // 生成组件
        treePanel = new DynamicTree();
        createTree();
        decorateTree();

        createButtons1();
        // 调整大小
        treePanel.setPreferredSize(new Dimension(500, 400));
        add(treePanel, BorderLayout.CENTER);

        createButtons2();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Commands command = Commands.valueOf(event.getActionCommand());
        switch (command) {
            case ADD:
                treePanel.add();
                break;
            case REMOVE:
                treePanel.remove();
                break;
            case FETCH:
                treePanel.fetch();
                break;
            case REBASE:
                treePanel.rebase();
                break;
            default:
                log.error("Error Command!");
        }
    }

    /**
     * 生成树
     */
    private void createTree() {
        List<Repository> repositoryList = config2Repository(readConfig());
        generateRepositoryList(repositoryList);
        treePanel.setRepositoryList(repositoryList);

        // 生成树
        for (Repository repository : repositoryList) {
            DefaultMutableTreeNode rNode = treePanel.addObject(null, repository, true);
            for (Branch branch : repository.getBranchList()) {
                DefaultMutableTreeNode bNode = treePanel.addObject(rNode, branch, true);
            }
        }
    }

    /**
     * 装饰树
     */
    private void decorateTree() {
        Icon rIcon = new ImageIcon(Icons.REPOSITORY.toString());
        Icon bIcon = new ImageIcon(Icons.BRANCH.toString());
        // 树节点渲染器
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        render.setOpenIcon(rIcon);
        render.setClosedIcon(rIcon);
        render.setLeafIcon(bIcon);
        treePanel.setRenderer(render);
    }

    /**
     * 生成并添加按钮（拉取、变基）
     */
    private void createButtons1() {
        // 拉取按钮 = 拉取分支状态
        JButton fetchButton = createButton(Titles.FETCH, Commands.FETCH);
        // 变基按钮 = 变基
        JButton rebaseButton = createButton(Titles.REBASE, Commands.REBASE);

        // 添加到面板
        JPanel btnPanel = new JPanel(new GridLayout(0, 3));
        btnPanel.add(fetchButton);
        btnPanel.add(rebaseButton);
        add(btnPanel, BorderLayout.NORTH);
    }

    /**
     * 生成并添加按钮（新增、移除）
     */
    private void createButtons2() {
        // 新增按钮 = 新增仓库
        JButton addButton = createButton(Titles.ADD, Commands.ADD);
        // 移除按钮 = 移除仓库
        JButton removeButton = createButton(Titles.REMOVE, Commands.REMOVE);

        // 添加到面板
        JPanel btnPanel = new JPanel(new GridLayout(0, 3));
        btnPanel.add(addButton);
        btnPanel.add(removeButton);
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
            List<Config> list = JsonUtil.string2Bean(config, typeToken);
            return deduplicate(list);
        } catch (IOException e) {
            log.error("Read Config Error!", e);
        }
        return Collections.emptyList();
    }

    /**
     * 去重
     *
     * @param list 列表
     * @return 去重后的列表
     */
    private <T> List<T> deduplicate(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 生成并显示界面
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
        // Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
        // For thread safety, this method should be invoked from the event-dispatching thread.
        javax.swing.SwingUtilities.invokeLater(DynamicTreeDemo::createAndShowGUI);
    }
}
