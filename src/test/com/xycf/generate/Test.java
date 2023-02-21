package com.xycf.generate;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import com.xycf.generate.operator.ClassOperator;
import com.xycf.generate.util.EncryptUtil;
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

    public static void main(String[] args) {
        String abc = EncryptUtil.DESencode("abc", "123456");
        String s = EncryptUtil.DESdecode(abc, "123456");
        System.out.println(abc);
        System.out.println(s);
    }
}
