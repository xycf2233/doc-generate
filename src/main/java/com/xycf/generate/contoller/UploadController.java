package com.xycf.generate.contoller;

import com.xycf.generate.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author ztc
 * @Description 上传文件控制层
 * @Date 2023/2/2 17:06
 */
@RestController
@RequestMapping("/upload")
@Api(value = "上传文件控制层")
public class UploadController {
    @Resource
    private UploadService uploadService;

    @ApiOperation(value = "上传压缩文件")
    @PostMapping("/zip")
    public String uploadZip(@RequestParam("file") MultipartFile file){
        return uploadService.uploadFile(file);
    }
}
