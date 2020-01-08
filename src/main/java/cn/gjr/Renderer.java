package cn.gjr;

import cn.gjr.constants.Icons;
import cn.gjr.utils.GitUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * 渲染器
 *
 * @author GaoJunru
 */
public class Renderer extends DefaultTreeCellRenderer {
    /**
     * 仓库图标
     */
    private transient Icon repositoryIcon = new ImageIcon(Icons.REPOSITORY.toString());
    /**
     * 分支图标
     */
    private transient Icon branchIcon = new ImageIcon(Icons.BRANCH.toString());

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object obj = node.getUserObject();
        if (GitUtil.isRepository(obj)) {
            setIcon(repositoryIcon);
        } else if (leaf) {
            setIcon(branchIcon);
        }
        return this;
    }
}
