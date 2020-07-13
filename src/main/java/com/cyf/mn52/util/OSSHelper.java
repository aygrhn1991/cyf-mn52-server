package com.cyf.mn52.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class OSSHelper {

    @Value("${oss.endPoint}")
    private String endPoint;
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucketName}")
    private String bucketName;

    public boolean upload(List<String> fileName, List<File> file) {
        try {
            OSS ossClient = new OSSClientBuilder().build(this.endPoint, this.accessKeyId, this.accessKeySecret);
            for (int i = 0; i < fileName.size(); i++) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, "img/" + fileName.get(i), file.get(i));
                ossClient.putObject(putObjectRequest);
            }
            ossClient.shutdown();
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
