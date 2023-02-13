package com.xycf.generate;

import cn.hutool.core.util.ClassUtil;
import com.alibaba.fastjson2.JSON;
import com.sun.javadoc.ClassDoc;
import com.xycf.generate.contoller.TestController;
import com.xycf.generate.entity.FieldEntry;
import com.xycf.generate.operator.ClassOperator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/1 13:52
 */
@SpringBootTest(classes = GenerateApplication.class)
public class Test {

    @Resource
    private ClassOperator classOperator;

    @org.junit.jupiter.api.Test
    public void test(){
//        ClassDoc classDoc = classOperator.getClassDoc("D:\\project\\generate-interface-document\\src\\main\\java\\com\\xycf\\generate\\entity\\FieldEntry.java");
//        classOperator.getMethodsInfo();
    }
}
