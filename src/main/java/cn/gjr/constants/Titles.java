package cn.gjr.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 常量-标题
 *
 * @author GaoJunru
 */
@AllArgsConstructor
@Getter
public enum Titles {
    /**
     * 增加
     */
    TITLE_ADD("Add"),
    /**
     * 增加
     */
    TITLE_REFRESH("Refresh"),
    /**
     * 增加
     */
    TITLE_REMOVE("Remove");

    /**
     * 标题
     */
    String value;
}
