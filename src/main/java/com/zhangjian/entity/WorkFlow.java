package com.zhangjian.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class WorkFlow {
    @Id
    private String id;
    private String promptId;
    private Map<String,Object> prompt;
    private Boolean completed ;
    private List<String> images;
}
