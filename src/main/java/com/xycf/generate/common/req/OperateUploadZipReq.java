package com.xycf.generate.common.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ztc
 * @Description
 * @Date 2023/2/20 15:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperateUploadZipReq {

    @ApiModelProperty("用户唯一key")
    private String key;

    /**
     * 增的时候需要前端传当前这一级的文件夹ID
     * 删的时候根据文件ID删除
     * todo 改暂时不考虑
     */
    @ApiModelProperty("文件ID")
    private String fileId;
}
