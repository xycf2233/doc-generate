package com.xycf.generate.service.base;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author ztc
 * @Description 文档处理 父类
 * @Date 2023/1/31 14:08
 */
public interface DocService {

    /**
     * 解析xml
     * @param filePath
     */
    void parseXml(String filePath);

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
     * 根据用户上传的模板生成word文档
     * 1.解析解压后的文件 找出实体层文件和控制层文件
     * 2.解析控制层文件 得到 控制层中所有接口的信息 （入参、出参、请求方式、接口路径）
     *
     * @param key            每次操作的唯一标识   保证上传操作和生成文档操作的一致性，前端传
     * @param controllerDirs 用户声明的控制层文件夹 可为null
     * @param entityDirs     用户声明的实体层文件夹 不可为null
     * @param flag     是否根据默认模板生成文件
     * @return
     */
    String generateDocumentForTemplate(String key, List<String> controllerDirs, List<String> entityDirs,boolean flag);




}
