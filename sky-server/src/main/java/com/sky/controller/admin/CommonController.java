package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 通用控制类
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用功能接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传{}", file);
        //截取文件原始名称
        String originalFilename = file.getOriginalFilename();
        //截取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 构建新的文件名称
        String fileName = UUID.randomUUID().toString() + suffix;
        // 上传文件到阿里云oss
        try {
            String filePath = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(filePath);
        } catch (Exception e) {
            log.error(MessageConstant.UPLOAD_FAILED, e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
