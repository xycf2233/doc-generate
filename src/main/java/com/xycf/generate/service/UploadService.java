package com.xycf.generate.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ztc
 * @Description 上传文件 服务层
 * @Date 2023/1/31 17:23
 */
public interface UploadService {

    /**
     * 上传文件（压缩包 zip）
     * @param multipartFile
     */
    String uploadFile(MultipartFile multipartFile);
}
