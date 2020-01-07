package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Config;
import cn.gjr.bean.Repository;
import cn.gjr.constants.Commands;
import cn.gjr.constants.Icons;
import cn.gjr.constants.Titles;
import cn.gjr.utils.GitUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
        Base base = new Base();
        List<Config> configList = base.readConfig();
        List<Repository> repositoryList = base.config2Repository(configList);
        base.generateRepositoryList(repositoryList);
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
