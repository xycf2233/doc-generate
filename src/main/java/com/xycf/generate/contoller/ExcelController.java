package com.xycf.generate.contoller;

import com.xycf.generate.common.base.BaseResponse;
import com.xycf.generate.common.req.OperationExcelReq;
import com.xycf.generate.entity.excel.ExcelEntity;
import com.xycf.generate.entity.excel.ExcelListResponse;
import com.xycf.generate.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @FileName ExcelController
 * @Author lin.jiang
 * @Date 2023/2/20 11:24
 * @Description:
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    ExcelService excelService;

    /**
     * 解析两个sheet页数据 返回前端
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/getExcelList", method = RequestMethod.POST)
    public BaseResponse<ExcelListResponse> getExcelListResponse(@RequestParam("file") MultipartFile file) {
        try {
            return BaseResponse.success(excelService.getExcelListResponse(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 合并excel
     *
     * @param req
     * @return
     */
    @PostMapping("/mergeExcel")
    public BaseResponse<Void> mergeExcel(@RequestBody OperationExcelReq req, HttpServletResponse response) {
        excelService.mergeExcel(req, response);
        return BaseResponse.success();
    }

    /**
     * 合并excel
     *
     * @param file1
     * @param file2
     * @param response
     * @return
     */
    @PostMapping("/mergeExcelV2")
    public BaseResponse<Void> mergeExcelV2(@RequestParam("file") MultipartFile file,
                                           @RequestParam("columnToMerge") Integer columnToMerge,
                                           HttpServletResponse response) {
        excelService.mergeExcel(file,columnToMerge ,response);
        return BaseResponse.success();
    }

    public BaseResponse<Void> download(HttpServletResponse response, HttpServletRequest request) {
        excelService.download(response, request);
        return BaseResponse.success();
    }
}
