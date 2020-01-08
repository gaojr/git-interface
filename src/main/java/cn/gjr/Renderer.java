package cn.gjr;

import cn.gjr.bean.Repository;
import cn.gjr.constants.Icons;

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
        if (isRepository(value)) {
            setIcon(repositoryIcon);
        } else if (leaf) {
            setIcon(branchIcon);
        }
        return this;
    }

    /**
     * 是否为仓库对象
     *
     * @param value 对象
     * @return {@code true} 是
     */
    private boolean isRepository(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return node.getUserObject() instanceof Repository;
    }
}
