package com.micro.ssyx.product.controller;

import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.product.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author micro
 * @description
 * @date 2024/4/28 19:34
 * @github https://github.com/microsbug
 */
@RestController
@RequestMapping(value = "/admin/product")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件路径
     */
    @PostMapping("fileUpload")
    public ResultResponse<String> fileUpload(final MultipartFile file) {
        return ResultResponse.ok(fileUploadService.fileUpload(file));
    }
}
