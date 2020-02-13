package cn.gjr.frame;

import cn.gjr.Renderer;
import cn.gjr.bean.Repository;
import cn.gjr.constants.Commands;
import cn.gjr.constants.Titles;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
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
    /**
     * 动态树
     */
    private DynamicTree treePanel;

    /**
     * 构造函数
     *
     * @param repositories 仓库
     * @param groups 分组
     */
    public DynamicTreeDemo(List<Repository> repositories, List<String> groups) {
        super(new BorderLayout());
        setOpaque(true);

        // 生成组件
        treePanel = new DynamicTree();
        treePanel.createGroupNode(groups);
        treePanel.createTree(repositories);
        treePanel.setRenderer(new Renderer());

        createButtons1();
        add(treePanel, BorderLayout.CENTER);
        createButtons2();
        treePanel.expandTree();
    }

    /**
     * 生成并显示界面
     *
     * @param repositories 仓库
     * @param groups 分组
     * @return 面板
     */
    public static DynamicTreeDemo createAndShowGUI(List<Repository> repositories, List<String> groups) {
        return new DynamicTreeDemo(repositories, groups);
    }

    /**
     * 触发动作
     *
     * @param event 事件
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        Commands command = Commands.valueOf(event.getActionCommand());
        switch (command) {
            case GROUP:
                treePanel.addGroup();
                break;
            case REPO:
                treePanel.addRepo();
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
     * 生成并添加按钮（拉取、变基）
     */
    private void createButtons1() {
        // 拉取按钮 = 拉取分支状态
        JButton fetchButton = createButton(Titles.FETCH, Commands.FETCH);
        // 变基按钮 = 变基
        JButton rebaseButton = createButton(Titles.REBASE, Commands.REBASE);

        // 添加到面板
        JPanel btnPanel = new JPanel(new GridLayout(0, 2));
        btnPanel.add(fetchButton);
        btnPanel.add(rebaseButton);
        add(btnPanel, BorderLayout.NORTH);
    }

    /**
     * 生成并添加按钮（新增、移除）
     */
    private void createButtons2() {
        // 分组按钮 = 新增分组
        JButton groupButton = createButton(Titles.ADD_GROUP, Commands.GROUP);
        // 新增按钮 = 新增仓库
        JButton addButton = createButton(Titles.ADD_REPO, Commands.REPO);
        // 移除按钮 = 移除仓库
        JButton removeButton = createButton(Titles.REMOVE, Commands.REMOVE);

        // 添加到面板
        JPanel btnPanel = new JPanel(new GridLayout(0, 3));
        btnPanel.add(groupButton);
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
     * 节点转仓库list
     *
     * @return 仓库list
     */
    public List<Repository> getRepositories() {
        return treePanel.getRepositories();
    }

    /**
     * 节点转分组list
     *
     * @return 分组list
     */
    public List<String> getGroups() {
        return treePanel.getGroups();
    }
}
