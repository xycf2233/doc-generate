package com.xycf.generate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;


/**
 * @author Administrator
 * @description 控制层注解枚举
 * @date 2023/2/4 17:14
 */
@Getter
@AllArgsConstructor
public enum ControllerAnnotationEnum {
    CONTROLLER("Controller"),
    REST_CONTROLLER("RestController"),
    ;
    private String code;
    private static final Set<ControllerAnnotationEnum> ALL = EnumSet.allOf(ControllerAnnotationEnum.class);

    /**
     * 判断是否是控制层注解
     * @param annotationName
     * @return
     */
    public static boolean isControllerAnnotation(String annotationName){
        return ALL.stream().anyMatch(t->t.getCode().equals(annotationName));
    }
}
