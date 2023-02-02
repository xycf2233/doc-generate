package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

/**
 * @Author ztc
 * @Description 支持的压缩文件枚举
 * @Date 2023/2/2 16:37
 */
@Getter
@AllArgsConstructor
public enum DecompressionEnum {
    ZIP(".zip"),
    RAR(".rar"),
    SEVEN_Z(".7z"),
    ;
    private final String code;

    private static final Set<DecompressionEnum> ALL = EnumSet.allOf(DecompressionEnum.class);

    public static boolean containsSuffix(String fileName){
        return ALL.stream().anyMatch(t -> fileName.equals(t.code));
    }
}
