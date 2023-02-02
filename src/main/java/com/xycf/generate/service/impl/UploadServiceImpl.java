package com.xycf.generate.service.impl;

import cn.hutool.core.lang.ObjectId;
import com.xycf.generate.common.enums.DecompressionEnum;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.service.UploadService;
import com.xycf.generate.service.base.DecompressionService;
import com.xycf.generate.util.FileUtil;
import com.xycf.generate.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;

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

    /**
     * 前端传压缩文件-》保存压缩文件-》解压-》返回唯一标识
     * @param multipartFile
     * @return 唯一标识
     */
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        //3.生成唯一标识
        String res = ObjectId.next();

        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.indexOf("."));
        if(!DecompressionEnum.containsSuffix(suffix)){
            throw new AppException("仅支持上传和解压rar、7z、zip压缩文件！");
        }
        //加入唯一标识  使用户之间文件隔离
        String dir = docConfig.getZip()+File.separator+res;
        FileUtil.mkDir(dir);
        String path =dir+File.separator+originalFilename;
        File file = new File(path);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        //1.保存压缩文件
        try  {
            //获取项目路径
            bis = new BufferedInputStream(multipartFile.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] b = new byte[1024];
            int read = bis.read(b);
            while(read!=-1){
                bos.write(b);
                read = bis.read(b);
            }
            bos.flush();
            log.info("-------保存压缩文件到临时文件夹-------");
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("保存压缩文件失败");
        }finally {
            try {
                if(bos!=null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(bis!=null){
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        //2.解压->删除zip
        decompressionService.decompression(path, docConfig.getUpZip()+File.separator+res, suffix);

        //4.存入缓存
        redisUtils.setCacheObject(RedisConstants.UPLOAD_ZIP+res,path);
        return res;
    }
}
