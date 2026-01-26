package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {

    /**
     * 自定义定时任务方法
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void doTask() {
        log.info("自定义定时任务执行了 每5秒执行一次 {}", System.currentTimeMillis());
    }

}
