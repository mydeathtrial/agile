package com.agile.common.task;

import com.agile.common.mvc.service.TaskService;
import com.agile.common.util.ObjectUtil;
import com.agile.mvc.entity.SysTaskEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

/**
 * @author 佟盟
 * @version 1.0
 * 日期 2019/2/28 20:17
 * 描述 定时任务信息
 * @since 1.0
 */
@Setter
@Getter
public class TaskInfo {
    /**
     * 触发器
     */
    private TaskTrigger trigger;
    /**
     * 任务
     */
    private TaskJob job;
    private ScheduledFuture scheduledFuture;

    public TaskInfo(SysTaskEntity sysTaskEntity, TaskTrigger trigger, TaskJob job, ScheduledFuture scheduledFuture) {
        ObjectUtil.copyProperties(sysTaskEntity, this);
        this.trigger = trigger;
        this.job = job;
        this.scheduledFuture = scheduledFuture;
    }

}