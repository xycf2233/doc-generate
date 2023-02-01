package com.xycf.generate.service.base;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ztc
 * @Description 解压服务层
 * @Date 2023/2/1 10:21
 */
public interface DecompressionService {

    /**
     * 解压缩文件到同一文件夹下
     * [.rar .zip .7z]
     *
     * @param compressedFilePath 压缩文件的路径，
     * @param targetPath         解压后保存的路径
     * @param suffix             压缩文件后缀名
     */
    String decompression(String compressedFilePath, String targetPath, String suffix);
}
