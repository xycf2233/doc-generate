package com.xycf.generate.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author ztc
 * @Description 文档管理相关配置
 * @Date 2023/1/31 15:38
 */
@Component
@Data
public class DocConfig {

    /**
     * 模板文件存放的目录
     */
    @Value(value = "doc.baseDir")
    private String baseDir;

    /**
     * word生成的输出目录
     */
    @Value(value = "doc.outputDir")
    private String outputDir;

    /**
     * 生成xml文件的保存路径
     */
    @Value(value = "${doc.xmlPath}")
    private String xmlFilePath;

    /**
     * 解压文件的保存路径
     */
    @Value(value = "${doc.upZip}")
    private String upZip;
}
