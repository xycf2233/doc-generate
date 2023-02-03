package com.xycf.generate;

import cn.hutool.core.util.ClassUtil;
import com.xycf.generate.contoller.TestController;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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


    @org.junit.jupiter.api.Test
    public void test(){
        Method[] methods = ClassUtil.getPublicMethods(TestController.class);
        for (Method method : methods) {
            //非controller接口不处理
            if(!isMvcMethod(method)){
                continue;
            }
            getRequestPath(method);
            //获取到入参类
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                ///获取类中所有入参
                Field[] fields = parameterType.getDeclaredFields();
                for (Field field : fields) {
//                    field.get
                }
            }
            System.out.println(parameterTypes);
            //获取出参类
            Class<?> returnType = method.getReturnType();
            System.out.println(returnType);
        }
    }

    private boolean isMvcMethod(Method method){
        //获取方法上所有注解
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof PostMapping){

                return true;
            }else if(annotation instanceof GetMapping){

                return true;
            }else if(annotation instanceof PutMapping){

                return true;
            }else if(annotation instanceof DeleteMapping){

                return true;
            }
        }
        return false;
    }

    private String getRequestPath(Method method){
        StringBuilder res = new StringBuilder();
        //获取方法上所有注解
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if(annotation instanceof PostMapping){
                String[] value = ((PostMapping) annotation).value();
                for (String v : value) {
                    if(res.length()!=0){
                        res.append(",");
                    }
                    res.append(v);
                }
            }else if(annotation instanceof GetMapping){
                String[] value = ((GetMapping) annotation).value();
                for (String v : value) {
                    if(res.length()!=0){
                        res.append(",");
                    }
                    res.append(v);
                }
            }else if(annotation instanceof PutMapping){
                String[] value = ((PutMapping) annotation).value();
                for (String v : value) {
                    if(res.length()!=0){
                        res.append(",");
                    }
                    res.append(v);
                }
            }else if(annotation instanceof DeleteMapping){
                String[] value = ((DeleteMapping) annotation).value();
                for (String v : value) {
                    if(res.length()!=0){
                        res.append(",");
                    }
                    res.append(v);
                }
            }
        }
        return res.toString();
    }
}
