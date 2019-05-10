package com.agile.common.util;

import com.agile.common.base.Constant;
import com.agile.common.properties.ApplicationProperties;

/**
 * @author 佟盟
 * @version 1.0
 * 日期： 2019/2/19 9:32
 * 描述： 主键生成工具
 * @since 1.0
 */
public class IdUtil {
    private static SnowflakeIdWorker snowflakeIdWorker;

    public static Long generatorId() {
        return getSnowflakeIdWorker().nextId();
    }

    public static SnowflakeIdWorker getSnowflakeIdWorker() {

        if (snowflakeIdWorker == null) {
            ApplicationProperties properties = FactoryUtil.getBean(ApplicationProperties.class);
            if (properties == null) {
                snowflakeIdWorker = new SnowflakeIdWorker(Constant.NumberAbout.ONE, Constant.NumberAbout.ONE);
            } else {
                snowflakeIdWorker = new SnowflakeIdWorker(properties.getWorkerId(), properties.getDataCenterId());
            }
        }
        return snowflakeIdWorker;
    }
}
