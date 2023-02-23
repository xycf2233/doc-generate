package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/23 16:57
 */
@Getter
@AllArgsConstructor
public enum OperationEnum {
    INSERT(0),
    DELETE(1),
    UPDATE(2),
    ;
    private int code;
}
