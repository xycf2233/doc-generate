package com.xycf.generate;

import com.xycf.generate.operator.ClassOperator;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
    public void test() {

    }
}
