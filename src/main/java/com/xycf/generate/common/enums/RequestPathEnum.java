package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Administrator
 * @description 请求路径 注解属性名 如PostMapping(value="xxx")  属性名为value
 * @date 2023/2/4 19:16
 */
@Getter
@AllArgsConstructor
public enum RequestPathEnum {
    VALUE("value"),
    ;
    private String code;

    private static final Set<RequestPathEnum> ALL = EnumSet.allOf(RequestPathEnum.class);

    /**
     * 获取请求方式
     * @param paramName 注解属性名称
     * @return 请求方式
     */
    public static boolean isRequestPath(String paramName){
        return ALL.stream().anyMatch(t -> t.getCode().equals(paramName));

    }
}
