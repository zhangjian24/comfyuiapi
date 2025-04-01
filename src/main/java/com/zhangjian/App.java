package com.zhangjian;

import com.zhangjian.api.IComfyuiAPI;
import com.zhangjian.service.IWrokflowService;
import com.zhangjian.ws.ComfyUIWSClient;
import io.minio.MinioClient;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

@EnableScheduling
@SpringBootApplication
@Log4j2
public class App implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${comfyui.wsurl}")
    private String wsurl;

    @Value("${comfyui.client-id}")
    private String clientId;

    @Value("${minio.bucket}")
    private String bucket;

    public static void main(String[] args){
        long s = System.currentTimeMillis();
        SpringApplication.run(App.class,args);
        log.info("start spent {}ms",(System.currentTimeMillis()-s));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//        IWrokflowService iWrokflowService = applicationReadyEvent.getApplicationContext().getBean(IWrokflowService.class);
//        MinioClient minioClient = applicationReadyEvent.getApplicationContext().getBean(MinioClient.class);
//        IComfyuiAPI iComfyuiAPI = applicationReadyEvent.getApplicationContext().getBean(IComfyuiAPI.class);
//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//
//        String uri = wsurl + "?clientId=" + clientId;
//        log.info("Connecting to  " + uri);
//        try {
//            container.connectToServer(new ComfyUIWSClient(iWrokflowService,minioClient,iComfyuiAPI,bucket), URI.create(uri));
//        } catch (DeploymentException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}
