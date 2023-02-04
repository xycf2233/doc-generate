package com.xycf.generate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.sun.javadoc.MethodDoc;
import com.xycf.generate.common.dto.ScanUnZipDirDTO;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.ClassEntry;
import com.xycf.generate.entity.ControllerOperatorBean;
import com.xycf.generate.operator.ClassOperator;
import com.xycf.generate.service.UploadService;
import com.xycf.generate.service.WordService;
import com.xycf.generate.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource
    private UploadService uploadService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ClassOperator classOperator;
    /**
     * 将上传的文件转换成xml文档
     *
     * @param multipartFile 上传的word文件
     * @return xml文件路径
     */
    @Override
    public String changeToXml(MultipartFile multipartFile) {
        Document document = new Document();
        InputStream inputStream = null;
        String originalFilename = multipartFile.getOriginalFilename();
        if (StrUtil.isEmpty(originalFilename)) {
            throw new RuntimeException("文件名不合法!");
        }
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        String xmlFileName = docConfig.getXmlFilePath() + File.separator + fileName + ".xml";
        try {
            inputStream = multipartFile.getInputStream();
            document.loadFromStream(inputStream, FileFormat.Word_Xml);
            document.saveToFile(xmlFileName);
            document.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("word转换xml文档成功!");
        return xmlFileName;
    }

    @Override
    public void uploadTemplate(MultipartFile multipartFile) {

    }

    @Override
    public void generateDocument(String key, HttpServletResponse response) {

    }

    /**
     * 生成word文档
     * 1.解析解压后的文件 找出实体层文件和控制层文件
     * 2.解析控制层文件 得到 控制层中所有接口的信息 （入参、出参、请求方式、接口路径）
     * @param key            每次操作的唯一标识   保证上传操作和生成文档操作的一致性，前端传
     * @param controllerDirs 用户声明的控制层文件夹 可为null
     * @param entityDirs     用户声明的实体层文件夹 不可为null
     * @return
     */
    @Override
    public String generateDocument(String key, List<String> controllerDirs, List<String> entityDirs) {
        if(CollUtil.isEmpty(entityDirs)){
            throw new AppException("声明的实体层文件夹不可为空！");
        }
        String redisKey = RedisConstants.UPLOAD_UNZIP + key;
        //获取解压的文件夹路径 redisUtils.setCacheObject(RedisConstants.UPLOAD_UNZIP+res,unZipDir);
        String unzipDir = redisUtils.getCacheObject(redisKey);

        //控制层文件map  k：类名  v：文件路径
        Map<String, String> controllerFileMap = new HashMap<>();
        //实体层文件map k：类名 v：文件路径
        Map<String, String> entityFileMap = new HashMap<>();

        uploadService.scanUnZipDir(new File(unzipDir), controllerDirs, entityDirs,controllerFileMap,entityFileMap);

        //获取解压后的控制层、实体层文件 -> 存入缓存
        redisUtils.setCacheMap(RedisConstants.CONTROLLER_DIR+key,controllerFileMap);
        redisUtils.setCacheMap(RedisConstants.ENTITY_DIR+key,entityFileMap);

        //解析控制层文件
        ControllerOperatorBean controllerOperatorBean = new ControllerOperatorBean();
        controllerFileMap.entrySet().stream().forEach(t->{
            //类名
            String className = t.getKey();
            //路径
            String value = t.getValue();
            //获取类中接口信息
            ControllerOperatorBean methodsInfo = classOperator.getMethodsInfo(key, value);
            System.out.println(methodsInfo);
        });
        return null;
    }

    private void parseControllerMap(){

    }
}
