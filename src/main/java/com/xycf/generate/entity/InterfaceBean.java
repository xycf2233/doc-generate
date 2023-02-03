package com.xycf.generate.entity;

import java.util.List;

/**
 * 接口详情 封装 入参  出参
 * @Author ztc
 * @Description 接口详情 封装 入参  出参
 * @Date 2023/1/31 17:11
 */
public class InterfaceBean {

    /**
     * 入参类
     */
    private List<Class> request;
    /**
     * 出参类
     */
    private Class response;
    /**
     * 请求方式
     */
    private String method;
}
