package com.xycf.generate.entity.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author ztc
 * @Description 控制层解析得到的实体
 * @Date 2023/1/31 17:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerOperatorBean {

    /**
     * k  接口名称  v 接口入参、出参、请求方式
     */
    private Map<String,InterfaceBean> interfaceBeanMap;

}
