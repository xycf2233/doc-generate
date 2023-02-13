package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Author ztc
 * @Description 设置默认值枚举
 * @Date 2023/2/13 16:13
 */
@Getter
@AllArgsConstructor
public enum DefaultEnum {
    LIST("List","[]"),
    ARRAY("[]","[]"),
    //引用数据类型
    BYTE_PACKAGE("Byte","0"),
    SHORT_PACKAGE("Short","0"),
    INTEGER("Integer","0"),
    LONG_PACKAGE("Long","0L"),
    FLOAT_PACKAGE("Float","0.0f"),
    DOUBLE_PACKAGE("Double","0.0d"),
    BOOLEAN_PACKAGE("Boolean","false"),
    CHARACTER("Character","\'u0000"),

    STRING("String","\"string\""),

    //基础数据类型 byte short int long float double boolean char
    BYTE("byte","0"),
    SHORT("short","0"),
    INT("int","0"),
    LONG("long","0L"),
    FLOAT("float","0.0f"),
    DOUBLE("double","0.0d"),
    BOOLEAN("boolean","false"),
    CHAR("char","\'u0000"),
    OTHER("Other","{}"),
    ;
    private String code;
    private String message;

    private static final Set<DefaultEnum> ALL = EnumSet.allOf(DefaultEnum.class);

    /**
     * 获取json默认值
     * @param type 类型名称
     * @return 请求方式
     */
    public static String getDefaultValue(String type){
        Optional<DefaultEnum> any = ALL.stream().filter(t -> t.getCode().equals(type)).findAny();
        if(any.isPresent()){
            return any.get().getMessage();
        }else{
            if(type.contains(ARRAY.getCode())){
                return ARRAY.getMessage();
            }
            return OTHER.getMessage();
        }
    }

    /**
     * 入参类型是否存在
     * @param type 入参类型
     * @return
     */
    public static boolean contains(String type){
        Optional<DefaultEnum> any = ALL.stream().filter(t -> t.getCode().equals(type)).findAny();
        if(any.isPresent()){
            return true;
        }else{
            if(type.contains(ARRAY.getCode())){
                return true;
            }
            return false;
        }
    }
}
