package com.xycf.generate.common.enums.base;

import com.xycf.generate.config.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/6 16:02
 */
@Getter
@AllArgsConstructor
public enum DocModelEnum {
    INTERFACE_ADDRESS("interfaceAddress", "接口地址"),
    METHOD("method", "请求方式"),
    REQUEST_BODY("requestBody", "入参示例json格式"),
    RESPONSE_BODY("responseBody", "出参示例json格式"),
    TITLE("title", "接口名称"),

    REQUEST_PARAM("requestParam", "入参"),
    REQUEST_PARAM_NAME("requestParam.name", "参数名"),
    REQUEST_PARAM_TYPE("requestParam.type", "类型"),
    REQUEST_PARAM_IS_MUST("requestParam.must", "是否必须"),
    REQUEST_PARAM_DEFAULT_VALUE("requestParam.defaultValue", "默认值"),
    REQUEST_PARAM_REMARKS("requestParam.remarks", "备注"),

    RESPONSE_PARAM("responseParam", "出参"),
    RESPONSE_PARAM_NAME("responseParam.name", "参数名"),
    RESPONSE_PARAM_TYPE("responseParam.type", "类型"),
    RESPONSE_PARAM_REMARKS("responseParam.remarks", "描述"),

    ;
    private String code;
    private String message;

    private static final Set<DocModelEnum> ALL = EnumSet.allOf(DocModelEnum.class);

    /**
     * 查询范式名称
     * @param code
     * @return
     */
    public static String getMessageByCode(String code){
        Optional<DocModelEnum> any = ALL.stream().filter(t -> t.getCode().equals(code)).findAny();
        if(!any.isPresent()){
            throw new AppException("未找到约定范式code："+code);
        }
        return String.valueOf(any.get().getMessage());
    }

}
