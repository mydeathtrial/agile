package com.agile.common.properties;

import com.agile.common.base.Constant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 佟盟
 * @version 1.0
 * @Date 2019/1/31 9:35
 * @Description TODO
 * @since 1.0
 */

@ConfigurationProperties(prefix = "agile")
@Setter
@Getter
public class ApplicationProperties {
    /**
     * 版本号
     */
    private String version;
    /**
     * 项目标题
     */
    private String title;
    /**
     * 工作ID (0~31)
     */
    long workerId = Constant.NumberAbout.ONE;
    /**
     * 数据中心ID (0~31)
     */
    long dataCenterId = Constant.NumberAbout.ONE;
}
