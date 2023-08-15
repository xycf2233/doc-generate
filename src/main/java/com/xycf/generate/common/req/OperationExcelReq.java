package com.xycf.generate.common.req;

import com.xycf.generate.entity.excel.ExcelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/23 16:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationExcelReq {

    /**
     * OperationExcelEnum:0 合并sheet
     */
    private Integer operation;

    /**
     * 关联列
     */
    private Integer columnToMerge;

    /**
     * 用户唯一key
     */
    private String key;

}
