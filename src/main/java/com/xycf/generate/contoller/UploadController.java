package com.xycf.generate.contoller;

import com.xycf.generate.common.base.BaseResponse;
import com.xycf.generate.common.req.OperateUploadZipReq;
import com.xycf.generate.entity.doc.ZipFile;
import com.xycf.generate.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

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
    public BaseResponse<String> uploadZip(@RequestParam("file")@ApiParam("上传的压缩文件") MultipartFile file){
        return BaseResponse.success(uploadService.uploadFile(file));
    }

    @ApiOperation(value = "上传模板文件")
    @PostMapping("/template")
    public BaseResponse<?> uploadTemplate(@RequestParam("file")@ApiParam("上传的模板文件") MultipartFile file,
                                          @RequestParam("key")@ApiParam("用户唯一key") String key){
        uploadService.uploadTemplate(file,key);
        return BaseResponse.success(null);
    }

    @ApiOperation("解压后的文件列表预览")
    @PostMapping("/uploadZipList")
    public BaseResponse<List<ZipFile>> uploadZipList(@RequestBody @ApiParam("用户唯一key") String key){
        List<ZipFile> list = uploadService.uploadZipList(key);
        return BaseResponse.success(list);
    }

    @ApiOperation("文件列表预览----删除")
    @PostMapping("/uploadZipList/del")
    public BaseResponse<?> delUploadZipList(@RequestBody OperateUploadZipReq req){
        uploadService.delUploadZipList(req);
        return BaseResponse.success(null);
    }

    @ApiOperation("文件列表预览----增加文件/文件夹")
    @PostMapping("/uploadZipList/add")
    public BaseResponse<?> addUploadZipList(OperateUploadZipReq req){
        uploadService.addUploadZipList(req);
        return BaseResponse.success(null);
    }
}
