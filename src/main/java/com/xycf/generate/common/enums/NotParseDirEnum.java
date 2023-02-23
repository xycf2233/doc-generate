package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Author ztc
 * @Description 不用解析的文件夹枚举
 * @Date 2023/2/20 15:00
 */
@Getter
@AllArgsConstructor
public enum NotParseDirEnum {
    GIT(".git"),
    IDEA(".idea"),
    TARGET("target"),
    TEST("test"),
    ;
    private String code;

    private static final Set<NotParseDirEnum> ALL = EnumSet.allOf(NotParseDirEnum.class);

    /**
     * 是否是不需要解析的文件夹
     * @param code 文件夹名称
     * @return
     */
    public static boolean contains(String code){
        Optional<NotParseDirEnum> any = ALL.stream().filter(t -> t.getCode().equals(code)).findAny();
        if(any.isPresent()){
            return true;
        }else{
            return false;
        }
    }
}
