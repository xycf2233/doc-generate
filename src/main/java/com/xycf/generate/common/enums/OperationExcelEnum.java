package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author ztc
 * @Description 操作excel枚举
 * @Date 2023/2/23 16:57
 */
@Getter
@AllArgsConstructor
public enum OperationExcelEnum {
    MERGE(0),
    ;
    private int code;
}
