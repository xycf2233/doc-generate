package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Author ztc
 * @Description 是否必须校验的注解枚举
 * @Date 2023/2/13 15:07
 */
@Getter
@AllArgsConstructor
public enum IsMustEnum {
    NOT_NULL("NotNull"),
    ;
    private String code;
    private static final Set<IsMustEnum> ALL = EnumSet.allOf(IsMustEnum.class);

    /**
     * 判断是否必须
     * @param annotationName 注解名称
     * @return 请求方式
     */
    public static boolean isMust(String annotationName){
        Optional<IsMustEnum> any = ALL.stream().filter(t -> t.getCode().equals(annotationName)).findAny();
        if(any.isPresent()){
            return true;
        }else{
            return false;
        }
    }
}
