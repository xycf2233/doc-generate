package com.xycf.generate.service.base;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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

    /**
     * 上传模板文件
     * @param multipartFile word/excel
     */
    void uploadTemplate(MultipartFile multipartFile);

    /**
     * 生成文档
     * @param key 每次操作的唯一标识   保证上传操作和生成文档操作的一致性，前端传
     */
    void generateDocument(String key, HttpServletResponse response);

    /**
     * 生成文档
     * @param key 每次操作的唯一标识   保证上传操作和生成文档操作的一致性，前端传
     * @return 文件路径
     */
    String generateDocument(String key);




}
