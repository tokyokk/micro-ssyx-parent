package com.micro.ssyx.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author micro
 * @description
 * @date 2024/4/28 19:35
 * @github https://github.com/microsbug
 */
public interface FileUploadService {

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @return 文件路径
     */
    String fileUpload(MultipartFile file);
}
