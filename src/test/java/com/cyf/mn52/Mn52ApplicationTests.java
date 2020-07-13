package com.cyf.mn52;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.InputStream;

@SpringBootTest
class Mn52ApplicationTests {

    @Test
    void contextLoads() {
//        try {
//            String endpoint = "http://oss-cn-beijing.aliyuncs.com";
//            String accessKeyId = "LTAI4G4ifSEhRjVSgQcUFYWW";
//            String accessKeySecret = "G7ams1ruiYx7FDAk9QI0wyt255egED";
//            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//            PutObjectRequest putObjectRequest = new PutObjectRequest("mn5200", "img/test/20200713/git.png", new File("E:/git.png"));
//            ossClient.putObject(putObjectRequest);
//            putObjectRequest = new PutObjectRequest("mn5200", "img/test/20200713/six.png", new File("E:/six.png"));
//            ossClient.putObject(putObjectRequest);
//            ossClient.shutdown();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
        System.out.println("hello");
    }

}
