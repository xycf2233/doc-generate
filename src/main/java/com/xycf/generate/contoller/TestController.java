package com.xycf.generate.contoller;

import com.xycf.generate.common.req.GenerateDocumentReq;
import com.xycf.generate.service.base.DecompressionService;
import com.xycf.generate.service.base.DocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/1/31 14:49
 */
@RestController
@Api(value = "测试")
public class TestController {
    @Resource(name = "wordServiceImpl")
    private DocService wordService;
    @Resource
    private DecompressionService decompressionService;


    @ApiOperation(value = "解压")
    @PostMapping(value = "/test1",name = "abc")
    public void test1(){
        String compressedFilePath = "C:\\Users\\张天成\\Desktop\\新建文件夹\\file\\1.zip";
        String targetPath="C:\\Users\\张天成\\Desktop\\新建文件夹\\file";
        String suffix=".zip";
        decompressionService.decompression(compressedFilePath,targetPath,suffix);
    }
    @PostMapping("/test2")
    public void test2(@NotNull String a,@RequestBody String b , Integer c){

    }

    @PostMapping("/generateDocument")
    public void generateDocument(@Valid @RequestBody GenerateDocumentReq req){
        String s = wordService.generateDocument(req.getKey(), req.getControllerDirs(), req.getEntityDirs());
    }

    @PostMapping("/generateDocumentForTemplate")
    public void generateDocumentForTemplate(@Valid @RequestBody GenerateDocumentReq req){
        String s = wordService.generateDocumentForTemplate(req.getKey(), req.getControllerDirs(), req.getEntityDirs());
    }
}
