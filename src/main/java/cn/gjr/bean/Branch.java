package cn.gjr.bean;

import lombok.Data;

import java.io.File;

/**
 * 分支
 *
 * @author GaoJunru
 */
@Data
public class Branch {
    /**
     * 目录
     */
    private File dir;
    /**
     * 名称
     */
    private String name;
    /**
     * 远端
     */
    private String upstream;
    /**
     * 是否当前分支
     */
    private boolean isCurrent = false;
    /**
     * 领先提交数量
     */
    private int ahead = 0;
    /**
     * 落后提交数量
     */
    private int behind = 0;
    /**
     * 新增文件数量
     */
    private int add = 0;
    /**
     * 修改文件数量
     */
    private int modify = 0;
    /**
     * 删除文件数量
     */
    private int delete = 0;

    @Override
    public String toString() {
        return String.format("%s%s[%s] ↑%d↓%d+%d~%d-%d", isCurrent ? "*" : " ", name, upstream, ahead, behind, add, modify, delete);
    }
}
