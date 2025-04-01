package com.zhangjian.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zhangjian.api.IComfyuiAPI;
import com.zhangjian.entity.WorkFlow;
import com.zhangjian.service.IWrokflowService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("workflow")
public class WorkflowController {

    @Autowired
    private IWrokflowService wrokflowService;

    @Autowired
    private IComfyuiAPI comfyuiAPI;

    @Autowired
    private MinioClient minioClient;

    @Value("${comfyui.client-id}")
    private String clientId;

    @Value("${minio.host}")
    private String host;

    @Value("${minio.bucket}")
    private String bucket;

    /**
     * 提交工作流
     * @param body
     * @return 工作流id
     */
    @PostMapping("/prompt")
    public ResponseEntity<Map<String,Object>> prompt(@RequestBody Map<String,Object> body){
        HashMap<String, Object> ret = new HashMap<>();

        Map<String,Object> creq = new HashMap<>();
        creq.put("prompt",body);
        creq.put("clientId",clientId);
        try {
            Response<JsonNode> promptResp = comfyuiAPI.prompt((new ObjectMapper()).writeValueAsString(creq)).execute();
            if(promptResp.isSuccessful()){
                String promptId = promptResp.body().get("prompt_id").asText();
                WorkFlow workFlow = new WorkFlow();
                workFlow.setCompleted(false);
                workFlow.setPrompt(body);
                workFlow.setPromptId(promptId);
                wrokflowService.save(workFlow);

                ret.put("promptId",promptId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(ret);
    }

    /**
     * 查询结果
     * @param promptId
     * @param response
     * @return 多个图片的压缩包
     */
    @GetMapping("/download/{promptId}")
    public ResponseEntity<String> download(@PathVariable("promptId") String promptId, HttpServletResponse response){
        try {
            Optional<WorkFlow> workFlow = wrokflowService.findByPromptId(promptId);
            if(workFlow.isPresent() && workFlow.get().getCompleted()){
                //将图片放到压缩文件中
                String zipfilename =promptId + ".zip";
                File zipFile = File.createTempFile(promptId,"zip");
                FileOutputStream fos = new FileOutputStream(zipFile) ;
                ZipOutputStream zos= new ZipOutputStream(fos) ;

                for (String image : workFlow.get().getImages()) {
                    GetObjectResponse getres = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(image).build());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    IOUtils.copy(getres,byteArrayOutputStream);
                    //写入一个条目，我们需要给这个条目起个名字，相当于起一个文件名称
                    zos.putNextEntry(new ZipEntry(image));
                    //往这个条目中写入一定的数据
                    zos.write(byteArrayOutputStream.toByteArray());
                    //关闭条目
                    zos.closeEntry();
                }

                zos.close();

                response.reset();
                response.setContentType("application/octet-stream");
                response.setCharacterEncoding("utf-8");
                response.setContentLength((int) zipFile.length());
                response.setHeader("Content-Disposition", "attachment;filename=" + zipfilename );

                ServletOutputStream outputStream = response.getOutputStream();
                IOUtils.copy(new FileInputStream(zipFile), outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("fail");
        }
        return ResponseEntity.ok("success");
    }
    /**
     * 查询结果
     * @param promptId
     * @param response
     * @return
     */
    @GetMapping("/view/{promptId}")
    public ResponseEntity<List<String>> view(@PathVariable("promptId") String promptId, HttpServletResponse response){
        Optional<WorkFlow> workFlow = wrokflowService.findByPromptId(promptId);
        if(workFlow.isPresent() && workFlow.get().getCompleted()){
            return ResponseEntity.ok(workFlow.get().getImages().stream().map(s->String.format("%s/%s/%s",host,bucket,s)).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

}
