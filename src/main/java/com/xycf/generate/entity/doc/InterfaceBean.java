package com.xycf.generate.entity.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接口详情 封装 入参  出参
 * @Author ztc
 * @Description 接口详情 封装 入参  出参
 * @Date 2023/1/31 17:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceBean {

    /**
     * 入参类
     */
    private List<ClassEntry> request;
    /**
     * 出参类
     */
    private ClassEntry response;
    /**
     * 请求方式
     */
    private String method;
    /**
     * 请求路径
     */
    private String path;

    /**
     * 入参示例json格式
     */
    private String requestBody;

    /**
     * 出参示例json格式
     */
    private String responseBody;

}
