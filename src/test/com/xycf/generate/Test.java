package com.xycf.generate;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/1 13:52
 */
@SpringBootTest(classes = GenerateApplication.class)
public class Test {
    @Resource(name = "StringRedisTemplate")
    RedisTemplate redisTemplate;


    /**
     * 测试缓存
     */
    @org.junit.jupiter.api.Test
    public void test(){
        redisTemplate.opsForValue().set("aaa","123");
    }
}
