package cn.gjr.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.List;

/**
 * 仓库
 *
 * @author GaoJunru
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Repository extends Config {
    /**
     * 目录
     */
    private File dir;
    /**
     * 分支
     */
    private List<Branch> branchList;
}
