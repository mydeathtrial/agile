package com.agile.mvc.service;

import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.jpa.dao.Dao;
import cloud.agileframework.spring.util.spring.IdUtil;
import cloud.agileframework.task.RunDetail;
import cloud.agileframework.task.Target;
import cloud.agileframework.task.Task;
import cloud.agileframework.task.TaskService;
import com.agile.common.base.Constant;
import com.agile.mvc.entity.SysApiEntity;
import com.agile.mvc.entity.SysBtTaskApiEntity;
import com.agile.mvc.entity.SysTaskDetailEntity;
import com.agile.mvc.entity.SysTaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2019/5/9 16:04
 * 描述 定时任务持久层操作
 * @version 1.0
 * @since 1.0
 */
@Component
public class TaskMySqlServiceImpl implements TaskService {
    @Autowired
    private Dao dao;

    @Override
    public List<Task> getTask() {
        List<SysTaskEntity> list = dao.findAll(SysTaskEntity.builder().build());
        return new ArrayList<>(list);
    }

    @Override
    public List<Target> getApisByTaskCode(long code) {
        List<SysApiEntity> list = dao.findAll(
                "select a.* from sys_api a left join sys_bt_task_api b on b.sys_api_id = a.sys_api_id where b.sys_task_id = ? order by b.order",
                SysApiEntity.class, code);
        return new ArrayList<>(list);
    }

    @Override
    public List<Task> getTasksByApiCode(String code) {
        List<SysTaskEntity> list = dao.findAll("SELECT\n" +
                "sys_task.sys_task_id,\n" +
                "sys_task.`name`,\n" +
                "sys_task.`status`,\n" +
                "sys_task.`enable`,\n" +
                "sys_task.cron,\n" +
                "sys_task.sync,\n" +
                "sys_task.update_time,\n" +
                "sys_task.create_time\n" +
                "FROM\n" +
                "sys_task\n" +
                "LEFT JOIN sys_bt_task_api ON sys_bt_task_api.sys_task_id = sys_task.sys_task_id\n" +
                "LEFT JOIN sys_api ON sys_bt_task_api.sys_api_id = sys_api.sys_api_id\n" +
                "WHERE\n" +
                "sys_api.`name` = ?\n", SysTaskEntity.class, code);
        return new ArrayList<>(list);
    }

    private Long save(String targetCode) {
        SysApiEntity api = dao.findOne(SysApiEntity.builder().name(targetCode).build());
        if (api != null) {
            return api.getSysApiId();
        } else {
            SysApiEntity entity = dao.saveOrUpdate(
                    SysApiEntity
                            .builder()
                            .type(false)
                            .sysApiId(IdUtil.generatorId())
                            .name(targetCode)
                            .build()
            );
            return entity.getSysApiId();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Task task) {

        dao.saveOrUpdate(task);

        for (Target target : task.targets()) {
            Long sysApiId = save(target.getCode());
            SysBtTaskApiEntity bt = dao.findOne(SysBtTaskApiEntity.builder()
                    .sysTaskId(task.getCode())
                    .sysApiId(sysApiId)
                    .build());
            if (bt == null) {
                dao.saveAndReturn(SysBtTaskApiEntity.builder()
                        .sysBtTaskApiId(IdUtil.generatorId())
                        .sysTaskId(task.getCode())
                        .sysApiId(sysApiId)
                        .order(Constant.NumberAbout.ONE)
                        .build()
                );
            }
        }

    }

    @Override
    public void remove(long id) {
        dao.deleteInBatch(dao.findAll(SysBtTaskApiEntity.builder().sysTaskId(id).build()));
        dao.deleteById(SysTaskEntity.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run(long taskCode) {
        SysTaskEntity task = dao.findOne(SysTaskEntity.class, taskCode);
        task.setStatus("2000");
        dao.update(task);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finish(long taskCode) {
        SysTaskEntity task = dao.findOne(SysTaskEntity.class, taskCode);
        task.setStatus("1000");
        dao.update(task);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void logging(RunDetail runDetail) {
        dao.save(SysTaskDetailEntity.builder()
                .sysTaskId(runDetail.getTaskCode())
                .sysTaskInfoId(IdUtil.generatorId())
                .ending(runDetail.isEnding())
                .startTime(runDetail.getStartTime())
                .endTime(runDetail.getEndTime())
                .log(runDetail.getLog().toString())
                .build()

        );
    }

    @Override
    public boolean setNxLock(long lockName, Date unlockTime) {
        synchronized (this) {
            //抢占锁
            return CacheUtil.lock(lockName, Duration.ofMillis((unlockTime.getTime() - System.currentTimeMillis())));
        }
    }

    @Override
    public void unLock(long lockName) {
        CacheUtil.unlock(lockName);
    }

    @Override
    public void enable(long id, boolean b) {
        //同步状态到数据库
        SysTaskEntity entity = dao.findOne(SysTaskEntity.class, id);
        entity.setEnable(b);
        dao.update(entity);
    }
}