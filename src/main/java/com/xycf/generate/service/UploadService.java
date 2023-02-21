package com.xycf.generate.service;

import com.xycf.generate.common.req.OperateUploadZipReq;
import com.xycf.generate.entity.doc.ZipFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

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

    /**
     * 扫描解压文件夹  将实体层文件和控制层文件
     * @param file 扫描的解压文件夹
     * @param controllerDirs 控制层文件夹名称集合
     * @param entityDirs 实体层文件夹名称集合
     * @return 扫描解压文件后的到的 控制层map 和 实体层map
     */
    void scanUnZipDir(File file, List<String> controllerDirs, List<String> entityDirs, Map<String, String> controllerFileMap,Map<String, String> entityFileMap);

    /**
     * 上传模板文件
     * @param file  模板文件
     * @param key   唯一序列
     * @return
     */
    void uploadTemplate(MultipartFile file, String key);

    /**
     * 获取压缩文件内容返回
     * @param key
     * @return
     */
    List<ZipFile> uploadZipList(String key);

    /**
     * 删除预览列表中某个文件
     * @param req
     */
    void delUploadZipList(OperateUploadZipReq req);

    /**
     * 在预览列表的某个文件夹下增加文件
     * @param req
     */
    void addUploadZipList(OperateUploadZipReq req);
}
