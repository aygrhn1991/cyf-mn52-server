package com.cyf.mn52;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@SpringBootTest
class Mn52ApplicationTests {

    @Autowired
    private JdbcTemplate jdbc;

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
        System.out.println("开始");
        String sql = "select DISTINCT t.gallery_unique_id from mn_image t where t.gallery_id is null";
        List<Map<String, Object>> images = this.jdbc.queryForList(sql);
        for (Map image : images) {
            try {
                sql = "select t.id from mn_gallery t where t.unique_id=?";
                int gallery_id = this.jdbc.queryForObject(sql, Integer.class, image.get("gallery_unique_id").toString());
                sql = "update mn_image t set t.gallery_id=? where t.gallery_unique_id=?";
                int count = this.jdbc.update(sql, gallery_id, image.get("gallery_unique_id").toString());
                System.out.println("更新图集--->" + gallery_id);
                System.out.println("更新数量--->" + count);
            } catch (Exception e) {
                System.out.println("问题图集--->" + image.get("gallery_unique_id").toString());
            }

        }
        System.out.println("全部完成");
    }

}
