package com.xycf.generate.service.impl;

import cn.hutool.core.util.StrUtil;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.service.WordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author ztc
 * @Description word文档处理实现类
 * @Date 2023/1/31 14:09
 */
@Service
@Slf4j
public class WordServiceImpl implements WordService {

    @Resource
    private DocConfig docConfig;

    /**
     * 将上传的文件转换成xml文档
     * @param multipartFile 上传的word文件
     * @return xml文件路径
     */
    @Override
    public String changeToXml(MultipartFile multipartFile) {
        Document document = new Document();
        InputStream inputStream =null;
        String originalFilename = multipartFile.getOriginalFilename();
        if(StrUtil.isEmpty(originalFilename)){
            throw new RuntimeException("文件名不合法!");
        }
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        String xmlFileName = docConfig.getXmlFilePath()+File.separator+ fileName+".xml";
        try {
            inputStream = multipartFile.getInputStream();
            document.loadFromStream(inputStream, FileFormat.Word_Xml);
            document.saveToFile(xmlFileName);
            document.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            document.close();
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("word转换xml文档成功!");
        return xmlFileName;
    }

    /**
     * 根据模板生成文档
     * 1.word->xml
     * 2.xml->ftl
     * 3.ftl->word
     * @param multipartFile
     * @return
     */
    @Override
    public String generateDocument(MultipartFile multipartFile) {
        changeToXml(multipartFile);
        return null;
    }
}
