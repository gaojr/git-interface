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
     * 分组图标
     */
    private static final Icon groupIcon = new ImageIcon(Icons.GROUP.getValue());
    /**
     * 仓库图标
     */
    private static final Icon repositoryIcon = new ImageIcon(Icons.REPOSITORY.getValue());
    /**
     * 分支图标
     */
    private static final Icon branchIcon = new ImageIcon(Icons.BRANCH.getValue());

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        // 图标
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object obj = node.getUserObject();
        if (GitUtil.isRepository(obj)) {
            setIcon(repositoryIcon);
        } else if (GitUtil.isBranch(obj)) {
            setIcon(branchIcon);
        } else {
            setIcon(groupIcon);
        }
        return this;
    }
}
