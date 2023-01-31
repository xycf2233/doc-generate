package com.xycf.generate.service.base;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ztc
 * @Description 文档处理 父类
 * @Date 2023/1/31 14:08
 */
public interface DocService {

    /**
     * 将上传的文件转换成xml文档
     * @param multipartFile 上传的word文件
     * @return xml文件路径
     */
    String changeToXml(MultipartFile multipartFile);

    String generateDocument(MultipartFile multipartFile);
}
