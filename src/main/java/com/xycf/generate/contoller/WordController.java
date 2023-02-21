package com.xycf.generate.contoller;

import com.xycf.generate.common.req.GenerateDocumentReq;
import com.xycf.generate.mapper.DocStrategyFactory;
import com.xycf.generate.service.base.DocService;
import com.xycf.generate.service.impl.WordServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author Administrator
 * @description word文档操作控制层
 * @date 2023/2/15 22:43
 */
@RestController
@RequestMapping("/word")
@Api("word文档操作控制层")
public class WordController {
    @Resource
    private DocStrategyFactory docStrategyFactory;

    @PostMapping("/generateDocument")
    @ApiOperation(value = "根据默认模板生成word文档")
    public void generateDocument(@Valid @RequestBody GenerateDocumentReq req){
        docStrategyFactory.generateDocumentDefault(WordServiceImpl.class.getName(),req.getKey(), req.getControllerDirs(), req.getEntityDirs());
    }

    @PostMapping("/generateDocumentForTemplate")
    @ApiOperation(value = "根据模板生成word文档")
    public void generateDocumentForTemplate(@Valid @RequestBody GenerateDocumentReq req){
        docStrategyFactory.generateDocumentForTemplate(WordServiceImpl.class.getName(),req.getKey(), req.getControllerDirs(), req.getEntityDirs());
    }
}
