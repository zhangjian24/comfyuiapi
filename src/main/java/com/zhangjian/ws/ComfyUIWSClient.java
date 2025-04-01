package com.zhangjian.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zhangjian.api.IComfyuiAPI;
import com.zhangjian.entity.WorkFlow;
import com.zhangjian.service.IWrokflowService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.Level;
import retrofit2.Response;

import javax.websocket.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ws客户端，接受comfyui的通知
 */
@ClientEndpoint
@Log4j2
@AllArgsConstructor
public class ComfyUIWSClient {

    private IWrokflowService iWrokflowService;

    private MinioClient minioClient;

    private IComfyuiAPI iComfyuiAPI;

    private String bucket;

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig){
        log.log(Level.INFO,"【{}】 on open session ",session.getId());
        session.getUserProperties().put("start",System.currentTimeMillis());
    }

    @OnError
    public void onError(Session session,Throwable t){
        log.log(Level.ERROR," on error ",t);
    }

    @OnClose
    public void onClose(Session session,CloseReason closeReason){
        log.log(Level.INFO," on close {}",closeReason.getReasonPhrase());
    }

    @OnMessage
    public void onMessage(Session session,String textMessage){
        log.log(Level.INFO,"【{}】 on message： {}",session.getId(),textMessage);
        try {
//            {"type": "executed", "data": {"node": "3", "display_node": "3", "output": {"images": [{"filename": "ComfyUI_00001_.png", "subfolder": "", "type": "output"}, {"filename": "ComfyUI_00002_.png", "subfolder": "", "type": "output"}, {"filename": "ComfyUI_00003_.png", "subfolder": "", "type": "output"}, {"filename": "ComfyUI_00004_.png", "subfolder": "", "type": "output"}]}, "prompt_id": "7a1f9287-616e-4b35-ab36-d54de8f8d610"}}
            JsonNode data = (new ObjectMapper()).readValue(textMessage,JsonNode.class);
            if("executed".equals(data.get("type").asText())){//工作流执行完成
                String promptId = data.get("data").get("prompt_id").asText();
                Optional<WorkFlow> optionalWorkFlow = iWrokflowService.findByPromptId(promptId);
                optionalWorkFlow.ifPresent(workFlow -> {
                    List<String> images = new ArrayList<>();
                    ArrayNode imgs = (ArrayNode)data.get("data").get("output").get("images");
                    for (int i = 0; i < imgs.size(); i++) {
                        try {
                            Response<ResponseBody> vres = iComfyuiAPI.view(imgs.get(i).get("subfolder").asText(), imgs.get(i).get("filename").asText(), imgs.get(i).get("type").asText()).execute();
                            String filename = imgs.get(i).get("filename").asText();
                            String suffix = filename.substring(filename.indexOf("."));
                            filename = filename.substring(0,filename.indexOf("."))+System.currentTimeMillis()+"."+ suffix;
                            minioClient.putObject(PutObjectArgs.builder()
                                            .bucket(bucket).object(filename).contentType("image/"+suffix).stream(vres.body().byteStream(), vres.body().contentLength(),5*1024*1024)
                                    .build());
                            images.add(filename);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    workFlow.setImages(images);
                    iWrokflowService.save(workFlow);
                });
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
