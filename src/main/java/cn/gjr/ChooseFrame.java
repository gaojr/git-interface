package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Repository;
import cn.gjr.constants.Commands;
import cn.gjr.constants.Titles;
import cn.gjr.utils.GitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * 选择目录
 *
 * @author GaoJunru
 */
@Slf4j
public class ChooseFrame extends JPanel implements ActionListener {
    private JFrame frame;
    private DynamicTree tree;

    public ChooseFrame(DynamicTree dynamicTree) {
        super(new GridLayout(0, 2));

        tree = dynamicTree;

        // 选择文件
        JFileChooser chooser = new JFileChooser();
        // 只选择文件夹
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile().getAbsoluteFile();
            if (GitUtil.isRepository(dir)) {
                showChoose(dir.getPath());
            } else {
                JOptionPane.showMessageDialog(tree, "选择的文件夹不是git仓库！", "非法操作", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * 显示选择框
     *
     * @param path 路径
     */
    private void showChoose(String path) {
        // 输入框
        JLabel nameLabel = new JLabel("name: ");
        JTextField nameField = new JTextField(FilenameUtils.getName(path));
        JLabel pathLabel = new JLabel("path: ");
        JTextField pathField = new JTextField(path);
        add(nameLabel);
        add(nameField);
        add(pathLabel);
        add(pathField);
        // 按钮
        JButton btn = new JButton(Titles.SAVE.getValue());
        btn.setActionCommand(Commands.SAVE.toString());
        btn.addActionListener(this);
        add(btn);

        frame = Base.createFrame("选择git仓库", this, 450, 200);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Commands command = Commands.valueOf(event.getActionCommand());
        if (Commands.SAVE.equals(command)) {
            Component[] components = getComponents();
            String name = ((JTextField) components[1]).getText();
            String path = ((JTextField) components[3]).getText();
            Repository repo = new Repository();
            repo.setName(name);
            repo.setPath(path);
            repo.setDir(new File(path));
            List<Branch> branchList = GitUtil.getBranchList(repo);
            repo.setBranchList(branchList);
            tree.add(repo);
            frame.dispose();
        }
    }
}
