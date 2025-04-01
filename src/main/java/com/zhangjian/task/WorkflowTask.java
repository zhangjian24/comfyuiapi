package com.zhangjian.task;

import com.zhangjian.service.IWrokflowService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class WorkflowTask {

    @Autowired
    private IWrokflowService iWrokflowService;

    @Scheduled(cron = "0/5 * * * * *")
    public void syncResultTask(){
        log.info("syncResultTask>>>>>>>>>>>>>>>>>>>>>");
        iWrokflowService.syncResultTask();
    }
}
