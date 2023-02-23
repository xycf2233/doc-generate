package com.xycf.generate.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson2.JSON;
import com.xycf.generate.entity.excel.ExcelEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ztc
 * @Description 监听读取excel文件
 * @Date 2023/2/14 15:13
 */
@Slf4j
@Component
@Order(2)
public class ExcelDataListener implements ReadListener<ExcelEntity> {

    private ThreadLocal<List<ExcelEntity>> threadLocal = null;

    public ExcelDataListener() {
        init();
    }

    private void init() {
        threadLocal = new ThreadLocal<>();
        threadLocal.set(new ArrayList<>());
    }

    public List<ExcelEntity> getList(){
        if(threadLocal==null){
            init();
        }
        return threadLocal.get();
    }

    public void add(ExcelEntity excelEntity){
        if(threadLocal==null){
            init();
        }
        threadLocal.get().add(excelEntity);
    }

    public void clear(){
        if(threadLocal!=null){
            threadLocal.remove();
        }
    }

    /**
     * 每读取一条数据时触发
     *
     * @param excelEntity
     * @param analysisContext
     */
    @Override
    public void invoke(ExcelEntity excelEntity, AnalysisContext analysisContext) {
        add(excelEntity);
    }

    /**
     * 全部读完后触发
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("读取excel文件已完成。。。。。。。。");
    }

    public void doClearList() {
        clear();
        log.info("清除list数据成功");
    }
}
