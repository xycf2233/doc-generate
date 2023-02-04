package com.xycf.generate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.ObjectId;
import com.sun.javadoc.ClassDoc;
import com.xycf.generate.common.dto.ScanUnZipDirDTO;
import com.xycf.generate.common.enums.DecompressionEnum;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.operator.ClassOperator;
import com.xycf.generate.service.UploadService;
import com.xycf.generate.service.base.DecompressionService;
import com.xycf.generate.util.FileUtil;
import com.xycf.generate.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void scanUnZipDir(File file, List<String> controllerDirs, List<String> entityDirs,Map<String, String> controllerFileMap,Map<String, String> entityFileMap) {
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
                if(unzipFile.getName().equals("test")||unzipFile.getName().equals("target")){
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
                    scanUnZipDir(unzipFile, controllerDirs, entityDirs,controllerFileMap,entityFileMap);
                }
            } else {
                if(!unzipFile.getName().endsWith(".java")){
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
                putEntityMap(entityFileMap, entityDir);
            } else {
                ClassOperator classOperator = new ClassOperator();
                ClassDoc classDoc = classOperator.getClassDoc(file.getAbsolutePath());
                if(classDoc!=null){
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
                putControllerMap(controllerFileMap, controllerDir);
            } else {
                ClassOperator classOperator = new ClassOperator();
                ClassDoc classDoc = classOperator.getClassDoc(file.getAbsolutePath());
                //类名
                String className = classOperator.getClassName(classDoc);
                controllerFileMap.put(className, file.getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\新建文件夹\\96540883.jpg");
        String name = file.getName();
        String absolutePath = file.getAbsolutePath();
        System.out.println(name);
    }
}