package com.xycf.generate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.text.CharSequenceUtil;
import com.sun.javadoc.ClassDoc;
import com.xycf.generate.common.enums.DecompressionEnum;
import com.xycf.generate.common.enums.NotParseDirEnum;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.common.req.OperateUploadZipReq;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.doc.ZipFile;
import com.xycf.generate.operator.ClassOperator;
import com.xycf.generate.service.UploadService;
import com.xycf.generate.service.base.DecompressionService;
import com.xycf.generate.util.EncryptUtil;
import com.xycf.generate.util.FileUtil;
import com.xycf.generate.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * @Author ztc
 * @Description 上传压缩文件服务层
 * @Date 2023/2/2 16:29
 */
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {
    @Resource
    private DecompressionService decompressionService;
    @Resource
    private DocConfig docConfig;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ClassOperator classOperator;

    /**
     * 前端传压缩文件-》保存压缩文件-》解压-》返回唯一标识
     *
     * @param multipartFile
     * @return 唯一标识
     */
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        //3.生成唯一标识
        String res = ObjectId.next();

        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.indexOf("."));
        if (!DecompressionEnum.containsSuffix(suffix)) {
            throw new AppException("仅支持上传和解压rar、7z、zip压缩文件！");
        }
        //加入唯一标识  使用户之间文件隔离
        String dir = docConfig.getZip() + File.separator + res;
        FileUtil.mkDir(dir);
        String path = dir + File.separator + originalFilename;
        File file = new File(path);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        //1.保存压缩文件
        try {
            //获取项目路径
            bis = new BufferedInputStream(multipartFile.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] b = new byte[1024];
            int read = bis.read(b);
            while (read != -1) {
                bos.write(b);
                read = bis.read(b);
            }
            bos.flush();
            log.info("-------保存压缩文件到临时文件夹-------");
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("保存压缩文件失败");
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        //2.解压->删除zip->将解压后的文件夹存入缓存
        String targetPath = docConfig.getUpZip() + File.separator + res;
        decompressionService.decompression(path, targetPath, suffix);
        redisUtils.setCacheObject(RedisConstants.UPLOAD_UNZIP + res, targetPath);
        //4.将压缩文件夹路径存入缓存
        redisUtils.setCacheObject(RedisConstants.UPLOAD_ZIP + res, path);

        return res;
    }

    /**
     * 扫描解压文件夹  将实体层文件和控制层文件
     *
     * @param file           扫描的解压文件夹
     * @param controllerDirs 控制层文件夹名称集合  可为null 为null时遍历所有文件
     * @param entityDirs     实体层文件夹名称集合 不可为null
     */
    @Override
    public void scanUnZipDir(File file, List<String> controllerDirs, List<String> entityDirs, Map<String, String> controllerFileMap, Map<String, String> entityFileMap) {
        if (!file.isDirectory()) {
            throw new AppException("未找到解压后的文件!");
        }
        if (CollUtil.isEmpty(entityDirs)) {
            throw new AppException("未声明实体层文件夹!");
        }
        //获取文件夹下所有文件
        File[] files = file.listFiles();
        for (File unzipFile : files) {
            if (unzipFile.isDirectory()) {
                //如果是test文件夹或者target文件夹跳过
                if (unzipFile.getName().equals("test") || unzipFile.getName().equals("target")) {
                    continue;
                }
                //如果是文件夹 判断是否是controllerDirs
                if (controllerDirs != null && controllerDirs.contains(unzipFile.getName())) {
                    //将该文件夹下所有的文件都视为控制层文件
                    putControllerMap(controllerFileMap, unzipFile);
                } else if (CollUtil.isNotEmpty(entityDirs) && entityDirs.contains(unzipFile.getName())) {
                    //判断是否是entityDirs
                    //将文件夹下所有文件视为 实体层文件
                    putEntityMap(entityFileMap, unzipFile);
                } else {
                    scanUnZipDir(unzipFile, controllerDirs, entityDirs, controllerFileMap, entityFileMap);
                }
            } else {
                if (!unzipFile.getName().endsWith(".java")) {
                    continue;
                }
                //这里主要处理controllerDirs为空的情况下 需要遍历所有文件找出控制层文件
                ClassDoc classDoc = classOperator.getClassDoc(unzipFile.getAbsolutePath());
                if (classDoc == null) {
                    continue;
                }
                //判断文件是否是controller文件
                boolean isController = classOperator.isController(classDoc);
                if (isController) {
                    //类名
                    String className = classOperator.getClassName(classDoc);
                    controllerFileMap.put(className, unzipFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 上传模板文件
     *
     * @param file
     * @param key
     * @return
     */
    @Override
    public void uploadTemplate(MultipartFile file, String key) {
        String dir = docConfig.getTemplate() + File.separator + key;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        FileUtil.mkDir(dir);
        String name = file.getOriginalFilename();
        try {
            bos = new BufferedOutputStream(new FileOutputStream(dir + File.separator + name));
            bis = new BufferedInputStream(file.getInputStream());
            byte[] b = new byte[1024];
            int read = bis.read(b);
            while (read != -1) {
                bos.write(b);
                read = bis.read(b);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //模板地址存入缓存
        redisUtils.setCacheObject(RedisConstants.TEMPLATE_DIR + key, dir + File.separator + name);
    }

    @Override
    public List<ZipFile> uploadZipList(String key) {
        List<ZipFile> zipFiles = redisUtils.getCacheObject(RedisConstants.preViewDoc + key);
        if (CollUtil.isNotEmpty(zipFiles)) {
            return zipFiles;
        }
        List<ZipFile> res = new ArrayList<>();
        //获取存储的路径 RedisConstants.UPLOAD_UNZIP + res
        String redisKey = RedisConstants.UPLOAD_UNZIP + key;
        String path = redisUtils.getCacheObject(redisKey);
        if (CharSequenceUtil.isEmpty(path)) {
            throw new AppException("未读取到保存的文件夹");
        }
        File srcFile = new File(path);
        if (!srcFile.isDirectory()) {
            throw new AppException("未读取到保存的文件夹");
        }
        getFileList(Objects.requireNonNull(srcFile.listFiles()), res);
        //将预览数据存入缓存
        redisUtils.setCacheObject(RedisConstants.preViewDoc + key, res);
        return res;
    }

    /**
     * 删除预览列表中某个文件
     *
     * @param req
     */
    @Override
    public void delUploadZipList(OperateUploadZipReq req) {
        List<ZipFile> zipFiles = redisUtils.getCacheObject(RedisConstants.preViewDoc + req.getKey());
        delZip(zipFiles, req.getFileId());
        redisUtils.setCacheObject(RedisConstants.preViewDoc + req.getKey(), zipFiles);
    }

    /**
     * 在预览列表的某个文件夹下增加文件
     *
     * @param req
     */
    @Override
    public void addUploadZipList(OperateUploadZipReq req) {
        List<ZipFile> zipFiles = redisUtils.getCacheObject(RedisConstants.preViewDoc + req.getKey());
        if(CharSequenceUtil.isNotEmpty(req.getAddDirName())){
            addDir(req.getAddDirName(),req.getFileId(),zipFiles);
        }else {
            addZip(zipFiles,req.getFileId(),req.getFile(),req.getKey());
        }
        redisUtils.setCacheObject(RedisConstants.preViewDoc + req.getKey(), zipFiles);
    }

    private void addDir(String addDirName, String fileId, List<ZipFile> zipFiles) {
        for (ZipFile zipFile : zipFiles) {
            if(zipFile.getFileId().equals(fileId) && zipFile.isDir()){
                String dirPath = EncryptUtil.DESdecode(zipFile.getFilePath(), docConfig.getSecret());
                StringBuilder targetPath = new StringBuilder(dirPath);
                targetPath.append(File.separator).append(addDirName);
                FileUtil.mkDir(targetPath.toString());
                ZipFile addFile = new ZipFile();
                addFile.setFilePath(targetPath.toString());
                addFile.setFileName(addDirName);
                addFile.setDir(true);
                zipFile.getZipFiles().add(addFile);
            }else if (zipFile.isDir()) {
                addDir(addDirName, fileId,zipFile.getZipFiles());
            }
        }
    }

    private void addZip(List<ZipFile> zipFiles, String fileId, MultipartFile file, String key) {
        String originalFilename = file.getOriginalFilename();
        if(!originalFilename.endsWith(".java")){
            throw new AppException("添加的不是一个java文件");
        }
        if (CharSequenceUtil.isEmpty(fileId)) {
            StringBuilder targetPath = new StringBuilder(docConfig.getUpZip())
                    .append(File.separator)
                    .append(key).append(File.separator).append(originalFilename);
            FileUtil.writeFile(file,targetPath.toString());
            ZipFile addFile = new ZipFile();
            addFile.setFilePath(targetPath.toString());
            addFile.setFileName(originalFilename.substring(0, originalFilename.indexOf(".")));
            zipFiles.add(addFile);
        }else{
            for (ZipFile zipFile : zipFiles) {
                if(zipFile.getFileId().equals(fileId) && zipFile.isDir()){
                    String dirPath = EncryptUtil.DESdecode(zipFile.getFilePath(), docConfig.getSecret());
                    StringBuilder targetPath = new StringBuilder(dirPath);
                    targetPath.append(File.separator).append(originalFilename);
                    FileUtil.writeFile(file,targetPath.toString());
                    ZipFile addFile = new ZipFile();
                    addFile.setFilePath(targetPath.toString());
                    addFile.setFileName(originalFilename.substring(0, originalFilename.indexOf(".")));
                    zipFile.getZipFiles().add(addFile);
                    break;
                }else if (zipFile.isDir()) {
                    addZip(zipFile.getZipFiles(), fileId,file,key);
                }
            }
        }

    }

    private void delZip(List<ZipFile> zipFiles, String fileId) {
        for (ZipFile zipFile : zipFiles) {
            if (zipFile.getFileId().equals(fileId)) {
                zipFiles.remove(zipFile);
                String path = EncryptUtil.DESdecode(zipFile.getFilePath(), docConfig.getSecret());
                File file = new File(path);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        FileUtil.deleteFolder(path);
                    } else {
                        FileUtil.deleteFile(path);
                    }
                    log.info("删除路径[{}]文件", path);
                } else {
                    log.warn("路径:[{}]下不存在文件", path);
                }
                break;
            }
            if (zipFile.isDir()) {
                delZip(zipFile.getZipFiles(), fileId);
            }
        }
    }

    private void getFileList(File[] files, List<ZipFile> res) {
        for (File file : files) {
            if (file.isDirectory() && !NotParseDirEnum.contains(file.getName())) {
                ZipFile zipFile = new ZipFile();
                zipFile.setFileName(file.getName());
                zipFile.setDir(true);
                zipFile.setFilePath(EncryptUtil.DESencode(file.getAbsolutePath(), docConfig.secret));
                getFileList(file.listFiles(), zipFile.getZipFiles());
                if (CollUtil.isNotEmpty(zipFile.getZipFiles())) {
                    res.add(zipFile);
                }
            } else {
                if (file.getName().endsWith(".java")) {
                    ZipFile zipFile = new ZipFile();
                    zipFile.setFileName(file.getName());
                    zipFile.setDir(false);
                    zipFile.setFilePath(EncryptUtil.DESencode(file.getAbsolutePath(), docConfig.secret));
                    res.add(zipFile);
                }
            }
        }
    }

    /**
     * 将文件夹下所有文件视为实体层文件放入map
     *
     * @param entityFileMap 实体层文件map
     * @param entityDir     实体层文件夹
     */
    private void putEntityMap(Map<String, String> entityFileMap, File entityDir) {
        if (!entityDir.isDirectory()) {
            throw new AppException("路径：" + entityDir + "不是一个文件夹");
        }
        File[] files = entityDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                putEntityMap(entityFileMap, file);
            } else {
                ClassOperator classOperator = new ClassOperator();
                ClassDoc classDoc = classOperator.getClassDoc(file.getAbsolutePath());
                if (classDoc != null) {
                    //类名
                    String className = classOperator.getClassName(classDoc);
                    entityFileMap.put(className, file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 将文件夹下所有文件视为控制层文件放入map
     *
     * @param controllerFileMap
     * @param controllerDir
     */
    private void putControllerMap(Map<String, String> controllerFileMap, File controllerDir) {
        if (!controllerDir.isDirectory()) {
            throw new AppException("路径：" + controllerDir + "不是一个文件夹");
        }
        File[] files = controllerDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                putControllerMap(controllerFileMap, file);
            } else {
                ClassOperator classOperator = new ClassOperator();
                ClassDoc classDoc = classOperator.getClassDoc(file.getAbsolutePath());
                //类名
                String className = classOperator.getClassName(classDoc);
                controllerFileMap.put(className, file.getAbsolutePath());
            }
        }
    }
}
