package com.xycf.generate.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @Author ztc
 * @Description doc工具类
 * @Date 2023/1/31 16:00
 */
@Slf4j
public class DocUtil {

    /**
     * 将xml文件转换成ftl文件
     * @param filePath xml文件路径
     * @param baseDir  模板保存地址
     * @return
     */
    public String changeToFtl(String filePath, String baseDir) {
        File file = new File(filePath);
        String fileName = file.getName();
        //模板文件存储路径
        StringBuilder ftlPath = new StringBuilder(baseDir).append(File.separator).append(fileName).append(".ftl");
        try (InputStream is = new FileInputStream(file);
             OutputStream os = new FileOutputStream(ftlPath.toString());) {
            byte[] b = new byte[1024];
            int read = is.read(b);
            while (read != -1) {
                os.write(b);
                read = is.read(b);
            }
            os.flush();
            log.info("----------------模板文件生成成功----------------");
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("----------------模板文件生成失败----------------");
        }
        return ftlPath.toString();
    }
}
