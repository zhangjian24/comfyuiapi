package com.zhangjian.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zhangjian.api.IComfyuiAPI;
import com.zhangjian.entity.WorkFlow;
import com.zhangjian.repository.WorkFlowRepository;
import com.zhangjian.service.IWrokflowService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Log4j2
public class WrokflowServiceImpl implements IWrokflowService {

    @Autowired
    private WorkFlowRepository workFlowRepository;

    @Autowired
    private IComfyuiAPI comfyuiAPI;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public Optional<WorkFlow> findByPromptId(String promptId) {
        WorkFlow param = new WorkFlow();
        param.setPromptId(promptId);
        return workFlowRepository.findOne(Example.of(param));
    }

    @Override
    public String save(WorkFlow workFlow) {
        workFlowRepository.save(workFlow);
        return workFlow.getId();
    }

    @Override
    public void syncResultTask() {
        WorkFlow param = new WorkFlow();
        param.setCompleted(false);
        Pageable pg = PageRequest.of(0,100);
        Page<WorkFlow> all = workFlowRepository.findAll(Example.of(param), pg);
        for (WorkFlow workFlow : all.getContent()) {
            try {
                log.info("begin workFlow={}",workFlow);
                retrofit2.Response<JsonNode> hisResponse = comfyuiAPI.history(workFlow.getPromptId()).execute();
                JsonNode jsonNode = hisResponse.body().get(workFlow.getPromptId());
                boolean completed = jsonNode.get("status").get("completed").asBoolean(false);
                if (completed) {
                    JsonNode images = jsonNode.get("outputs").get("3").get("images");//得到图片路径
                    if (images.isArray()) {
                        ArrayNode arrayNode = (ArrayNode) images;
                        List<String> imageList = new ArrayList<>();
                        for (int i = 0; i < arrayNode.size(); i++) {
                            //获取图片的二进制数据
                            retrofit2.Response<okhttp3.ResponseBody> imgFile = comfyuiAPI.view(arrayNode.get(i).get("subfolder").asText(),
                                    arrayNode.get(i).get("filename").asText(),
                                    arrayNode.get(i).get("type").asText()).execute();
                            if (imgFile.isSuccessful()) {
                                String filename = arrayNode.get(i).get("filename").asText();
                                String suffix = filename.substring(filename.indexOf(".")+1);
                                filename = filename.substring(0,filename.indexOf("."))+System.currentTimeMillis()+"."+ suffix;
                                minioClient.putObject(PutObjectArgs.builder()
                                        .bucket(bucket).object(filename).contentType("image/"+ suffix).stream(imgFile.body().byteStream(), imgFile.body().contentLength(),5*1024*1024)
                                        .build());
                                imageList.add(filename);
                            }
                        }
                        workFlow.setCompleted(true);
                        workFlow.setImages(imageList);
                        workFlowRepository.save(workFlow);
                        log.info("end workFlow={}",workFlow);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
