package com.xycf.generate.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {
    private static ThreadLocal<List<Map<Integer, String>>> threadLocal = null;

    private void init() {
        threadLocal = new ThreadLocal<>();
        threadLocal.set(new ArrayList<>());
    }

    public List<Map<Integer, String>> getList(){
        if(threadLocal==null){
            init();
        }
        return threadLocal.get();
    }

    public void add(Map<Integer, String> data){
        if(threadLocal==null || CollUtil.isEmpty(threadLocal.get())){
            init();
        }
        List<Map<Integer, String>> map = threadLocal.get();
        map.add(data);
    }

    public void clear(){
        if(threadLocal!=null){
            threadLocal.remove();
        }
    }
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }
}
