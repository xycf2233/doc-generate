package com.xycf.generate.contoller;

import com.xycf.generate.entity.InterfaceBean;
import com.xycf.generate.service.base.DecompressionService;
import com.xycf.generate.service.base.DocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/1/31 14:49
 */
@RestController
@Api(value = "测试")
public class TestController {
    @Resource
    private DocService docService;
    @Resource
    private DecompressionService decompressionService;

    @ApiOperation(value = "word转换xml")
    @PostMapping("/test")
    public void test(@RequestParam("file") MultipartFile file){
        docService.changeToXml(file);
    }


    @ApiOperation(value = "解压")
    @PostMapping("/test1")
    public void test1(){
        String compressedFilePath = "C:\\Users\\张天成\\Desktop\\新建文件夹\\file\\1.zip";
        String targetPath="C:\\Users\\张天成\\Desktop\\新建文件夹\\file";
        String suffix=".zip";
        decompressionService.decompression(compressedFilePath,targetPath,suffix);
    }
    @PostMapping("/test2")
    public void test2(String a,String b ,Integer c){

    }
}
