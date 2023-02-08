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
    IN_DESCRIPTION("${in-description}", "描述"),
    IN_IS_MUST("${in-isMust}", "是否必须"),
    IN_PARAM_NAME("${in-paramName}", "参数名"),
    IN_REMARKS("${in-remarks}", "备注"),
    IN_TYPE("${in-type}", "类型"),
    INTERFACE_ADDRESS("${interfaceAddress}", "接口地址"),
    METHOD("${method}", "请求方式"),
    OUT_DESCRIPTION("${out-description}", "描述"),
    OUT_IS_MUST("${out-isMust}", "是否必须"),
    OUT_PARAM_NAME("${out-paramName}", "参数名"),
    OUT_TYPE("${out-type}", "类型"),
    REQUEST("${request}", "入参示例json格式"),
    RESPONSE("${response}", "出参示例json格式"),
    TITLE("${title}", "接口名称"),
    TABLE("${table}", "表格"),
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
