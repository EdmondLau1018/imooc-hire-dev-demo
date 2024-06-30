package com.imooc.test;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

public class MinIOTest {

    @Test
    public void testMinIO() {

        try {
            //  创建 minio Java 客户端
            MinioClient minioClient =
                    MinioClient.builder()
                            //  minio 所在地址 (注意测试服务器在没有配置开启 SSL的 情况下 发送的是 http 请求)
                            .endpoint("http://192.168.32.100:9000")
                            //  登录 minio 的认证方式 客户杜纳用户名和密码
                            .credentials("imooc", "imooc1018")
                            .build();

            //  检测是否存在对应的桶 如果不存在则创建
            //  通过 Java 客户端判定对应的桶是否存在 如果不存在就创建
            String bucketName = "localjava";
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                System.out.println("Bucket " + bucketName + "  already exists.");
            }

            //  向刚刚创建的桶中上传文件
            minioClient.uploadObject(
                    //  在 uploadObject 中 构建 文件上传参数对象
                    UploadObjectArgs.builder()
                            //  桶名称
                            .bucket(bucketName)
                            //  上传文件的对象名称
                            .object("demo1.jpg")
                            //  上传文件的绝对路径
                            .filename("F:\\personal_codes_for_git\\imgs\\demo1.jpg")
                            .build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
