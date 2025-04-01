package com.zhangjian.repository;

import com.zhangjian.entity.WorkFlow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowRepository extends MongoRepository<WorkFlow,String> {

}
