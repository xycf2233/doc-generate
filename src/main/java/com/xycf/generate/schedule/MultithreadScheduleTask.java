package com.xycf.generate.schedule;

import com.xycf.generate.common.base.InitOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author ztc
 * @Description 多线程定时任务 后续优化：替代成xxl-job执行
 * @Date 2023/2/1 14:43
 */
@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync        // 2.开启多线程
@Slf4j
public class MultithreadScheduleTask {

    @Resource
    InitOperator initOperator;

    {
        log.info("---------多线程定时任务加载---------");
    }

    @Async
    @Scheduled(cron = "0 0 6 * * ?")  //间隔6小时
    public void clearSchedule() throws InterruptedException {
        initOperator.clearDir();
    }

}
