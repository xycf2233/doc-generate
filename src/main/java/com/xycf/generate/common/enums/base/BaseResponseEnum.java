package com.xycf.generate.common.enums.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author ztc
 * @Description 基础响应枚举
 * @Date 2022/12/13 11:11
 */
@Getter
@AllArgsConstructor
public enum BaseResponseEnum {
    SUCCESS("200",null),
    FAIL("500","业务处理失败"),
    ;
    private String code;
    private String message;
}
