package com.xycf.generate.common.base;

import com.xycf.generate.config.DocConfig;
import com.xycf.generate.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author ztc
 * @Description 项目初始化后进行的操作
 * @Date 2023/2/1 14:27
 */
@Component
@Slf4j
public class InitOperator implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private DocConfig docConfig;

    /**
     * 1.清除word文件夹下的所有文件
     * 2.清除xml文件夹下所有文件
     * 3.清除template文件夹下所有文件
     * 4.清除unZip文件夹下所有文件
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("---------项目启动完成，执行初始化操作---------");
//        clearDir();
    }

    public void clearDir(){
        FileUtil.deleteFolder(docConfig.getXmlFilePath());
        log.info("---------清空xml文件夹---------");
        FileUtil.deleteFolder(docConfig.getTemplate());
        log.info("---------清空template文件夹---------");
        FileUtil.deleteFolder(docConfig.getOutputDir());
        log.info("---------清空outFile文件夹---------");
        FileUtil.deleteFolder(docConfig.getUpZip());
        log.info("---------清空unZip文件夹---------");
    }
}
