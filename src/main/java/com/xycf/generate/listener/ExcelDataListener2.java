package com.xycf.generate.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
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
@Getter
@Component
@Order(3)
public class ExcelDataListener2 implements ReadListener<ExcelEntity> {

    private List<ExcelEntity> list = new ArrayList<>();

    /**
     * 每读取一条数据时触发
     *
     * @param excelEntity
     * @param analysisContext
     */
    @Override
    public void invoke(ExcelEntity excelEntity, AnalysisContext analysisContext) {
        list.add(excelEntity);
    }

    /**
     * 全部读完后触发
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext){
    }

    public void doClearList() {
        list.clear();
    }
}