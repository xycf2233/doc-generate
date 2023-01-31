package com.xycf.generate.contoller;

import com.xycf.generate.service.base.DocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
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

    @ApiOperation(value = "测试方法")
    @PostMapping("/test")
    public void test(@RequestParam("file") MultipartFile file){
        docService.changeToXml(file);
    }
}
