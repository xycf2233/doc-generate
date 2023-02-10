package com.xycf.generate.mapper;

import com.xycf.generate.service.base.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author ztc
 * @Description 文档处理策略类
 * @Date 2023/2/10 9:50
 */
@Component
public class DocStrategyFactory implements ApplicationContextAware {

    private Logger loggerFactory = LoggerFactory.getLogger(DocStrategyFactory.class);

    private Map<String, DocService> map = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DocService> beansOfType = applicationContext.getBeansOfType(DocService.class);
        beansOfType.values().forEach(t->{
            map.put(t.getClass().getName(),t);
        });
    }

    public void generateDocumentForTemplate(String className,String key, List<String> controllerDirs, List<String> entityDirs) {
        DocService docService = map.get(className);
        if (docService != null) {
            loggerFactory.info("文档处理策略工厂类调用-----");
            docService.generateDocumentForTemplate(key,controllerDirs,entityDirs);
        }
    }
}
