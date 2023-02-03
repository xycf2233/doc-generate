package com.xycf.generate.operator;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import com.xycf.generate.entity.ControllerOperatorBean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author ztc
 * @Description 控制层操作类
 * @Date 2023/1/31 17:04
 */
public class ControllerOperator {

    /**
     * 解析出控制层文件中 每一个接口 以及
     * 每个接口的入参Class、出参Class 请求方式
     * @param controllerFilePaths 控制层文件路径集合
     * @return  解析出来的内容
     */
    public ControllerOperatorBean resolveController(List<String> controllerFilePaths){

        return null;
    }

}
