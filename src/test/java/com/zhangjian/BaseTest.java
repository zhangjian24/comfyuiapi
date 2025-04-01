package com.zhangjian;

import com.zhangjian.service.IWrokflowService;
import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by zhangjian on 2018/2/7.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class BaseTest {

    @Resource
    private IWrokflowService wrokflowService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Test
    public void test() throws Exception{
        BucketExistsArgs arg = new BucketExistsArgs.Builder().bucket(bucket).build();
        System.out.println(minioClient.bucketExists(arg));

        String s = "Snipaste_2025-03-29_16-11-36.png";
        String filename = "D:\\WorkSpaces\\PersonSpace\\codes\\ComfyUIAPI\\image\\"+ s;
        UploadObjectArgs uarg = new UploadObjectArgs.Builder().bucket(bucket).contentType("image/png").object(s).filename(filename).build();
        ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(uarg);
        GetObjectArgs garg = new GetObjectArgs.Builder().bucket(bucket).object(s).build();
        GetObjectResponse getObjectResponse = minioClient.getObject(garg);
        IOUtils.copy(getObjectResponse,new FileOutputStream("aaaaaaaaa"+s));
    }
}

