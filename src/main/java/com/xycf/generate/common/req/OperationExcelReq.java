package com.xycf.generate.common.req;

import com.xycf.generate.entity.excel.ExcelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 0 增  1 删  2 改
     */
    private Integer operation;

    /**
     * 表格数据
     */
    private ExcelEntity excel;

    /**
     * 1  or  2
     */
    private Integer sheetNum;

    /**
     * 用户唯一key
     */
    private String key;

}
