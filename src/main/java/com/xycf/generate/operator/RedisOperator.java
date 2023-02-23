package com.xycf.generate.operator;

import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.entity.excel.ExcelEntity;
import com.xycf.generate.util.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/23 17:10
 */
@Component
public class RedisOperator {

    @Resource
    private RedisUtils redisUtils;

    public static String getExcelDataSheet1Key(String key){
        StringBuilder redisKey = new StringBuilder(RedisConstants.EXCEL_DATA).append(key).append(":sheet1");
        return redisKey.toString();
    }

    public static String getExcelDataSheet2Key(String key){
        StringBuilder redisKey = new StringBuilder(RedisConstants.EXCEL_DATA).append(key).append(":sheet2");
        return redisKey.toString();
    }

    public void setExcelDataSheet1(String key, List<ExcelEntity> listenerOneList){
        redisUtils.setCacheList(getExcelDataSheet1Key(key),listenerOneList);
    }

    public void setExcelDataSheet2(String key, List<ExcelEntity> listenerTwoList){
        redisUtils.setCacheList(getExcelDataSheet2Key(key),listenerTwoList);
    }

    /**
     * 获取缓存中的excel数据
     * @param excelDataRedisKey  getExcelDataSheet1Key/getExcelDataSheet2Key
     * @return
     */
    public List<ExcelEntity> getExcelDataSheet(String excelDataRedisKey){
        return redisUtils.getCacheList(excelDataRedisKey);
    }
}
