package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Administrator
 * @description TODO
 * @date 2023/2/4 19:07
 */
@Getter
@AllArgsConstructor
public enum RequestMethodEnum {
    POST("PostMapping","post"),
    GET("GetMapping","get"),
    PUT("PutMapping","put"),
    DELETE("DeleteMapping","delete"),
    ;
    private String code;
    private String message;

    private static final Set<RequestMethodEnum> ALL = EnumSet.allOf(RequestMethodEnum.class);

    /**
     * 获取请求方式
     * @param annotationName 注解名称
     * @return 请求方式
     */
    public static String isRequestMethod(String annotationName){
        Optional<RequestMethodEnum> any = ALL.stream().filter(t -> t.getCode().equals(annotationName)).findAny();
        if(any.isPresent()){
            return any.get().getMessage();
        }else{
            return null;
        }
    }
}
