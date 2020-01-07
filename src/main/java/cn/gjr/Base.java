package cn.gjr;

import cn.gjr.bean.Branch;
import cn.gjr.bean.Config;
import cn.gjr.bean.Repository;
import cn.gjr.utils.FileUtil;
import cn.gjr.utils.GitUtil;
import cn.gjr.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础方法
 *
 * @author GaoJunru
 */
@Slf4j
class Base {
    // TODO 写入配置

    /**
     * 读取配置
     *
     * @return 仓库list
     */
    List<Config> readConfig() {
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
     * 处理仓库列表
     *
     * @param configList 仓库list
     * @return 校正后的仓库list
     */
    List<Repository> config2Repository(List<Config> configList) {
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
     * 完善仓库列表
     * TODO 优化，提高速度
     *
     * @param repositoryList 仓库列表
     */
    void generateRepositoryList(List<Repository> repositoryList) {
        repositoryList.forEach(r -> {
            List<Branch> branches = GitUtil.getBranchList(r.getDir());
            r.setBranchList(branches);
        });
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
}
