package com.zhangjian.service;

import com.zhangjian.entity.WorkFlow;

import java.util.Optional;

public interface IWrokflowService {
    Optional<WorkFlow> findByPromptId(String promptId);
    String save(WorkFlow workFlow);
    void syncResultTask();
}
