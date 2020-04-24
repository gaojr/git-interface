package cn.gjr.gitinterface.frame;

import cn.gjr.gitinterface.Base;
import cn.gjr.gitinterface.constants.Commands;
import cn.gjr.gitinterface.constants.Titles;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 输入分组名
 *
 * @author GaoJunru
 */
@Slf4j
public class GroupFrame extends JPanel implements ActionListener {
    private JFrame frame;
    private DynamicTree tree;

    public GroupFrame(DynamicTree dynamicTree) {
        super(new GridLayout(0, 2));
        tree = dynamicTree;
        showChoose();
    }

    /**
     * 显示选择框
     */
    private void showChoose() {
        // 输入框
        JLabel nameLabel = new JLabel("name: ");
        JTextField nameField = new JTextField();
        add(nameLabel);
        add(nameField);
        // 按钮
        JButton btn = new JButton(Titles.SAVE.getValue());
        btn.setActionCommand(Commands.SAVE.toString());
        btn.addActionListener(this);
        add(btn);

        frame = Base.createFrame("输入分组名", this, 450, 200);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Commands command = Commands.valueOf(event.getActionCommand());
        if (Commands.SAVE.equals(command)) {
            Component[] components = getComponents();
            String name = ((JTextField) components[1]).getText();
            tree.addGroup(name);
            frame.dispose();
        }
    }
}
