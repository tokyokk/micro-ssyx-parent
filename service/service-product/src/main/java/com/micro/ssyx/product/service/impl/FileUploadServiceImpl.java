package com.micro.ssyx.product.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.micro.ssyx.product.service.FileUploadService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author micro
 * @description
 * @date 2024/4/28 19:35
 * @github https://github.com/microsbug
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${aliyun.endpoint}")
    private String endPoint;
    @Value("${aliyun.keyid}")
    private String accessKey;
    @Value("${aliyun.keysecret}")
    private String secreKey;
    @Value("${aliyun.bucketname}")
    private String bucketName;

    @Override
    public String fileUpload(final MultipartFile file) {
        try {
            // 创建OSSClient实例。
            final OSS ossClient = new OSSClientBuilder().build(endPoint, accessKey, secreKey);
            // 上传文件流。
            final InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            // 生成随机唯一值，使用uuid，添加到文件名称里面
            final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + fileName;
            // 按照当前日期，创建文件夹，上传到创建文件夹里面
            //  2021/02/02/01.jpg
            final String timeUrl = new DateTime().toString("yyyy/MM/dd");
            fileName = timeUrl + "/" + fileName;
            // 调用方法实现上传
            ossClient.putObject(bucketName, fileName, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            // 上传之后文件路径
            // https://ssyx-atguigu.oss-cn-beijing.aliyuncs.com/01.jpg
            // 返回
            return "https://" + bucketName + "." + endPoint + "/" + fileName;
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
