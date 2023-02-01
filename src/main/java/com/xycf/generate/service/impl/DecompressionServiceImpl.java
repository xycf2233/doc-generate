package com.xycf.generate.service.impl;

import com.xycf.generate.service.base.DecompressionService;
import com.xycf.generate.util.FileUtil;
import org.springframework.stereotype.Service;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/1 11:55
 */
@Service
public class DecompressionServiceImpl implements DecompressionService {


    /**
     * 解压缩文件到同一文件夹下
     * [.rar .zip .7z]
     *
     * @param compressedFilePath 压缩文件的路径，
     * @param targetPath         解压后保存的路径
     * @param suffix             压缩文件后缀名
     */
    @Override
    public String decompression(String compressedFilePath, String targetPath, String suffix) {
        return FileUtil.unCompressedFilesToSameDir(compressedFilePath, targetPath, suffix);
    }
}
