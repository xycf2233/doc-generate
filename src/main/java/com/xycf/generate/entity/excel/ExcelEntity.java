package com.xycf.generate.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xycf.generate.cache.LocalCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @Author ztc
 * @Description 操作excel文档 最大21列。
 * @Date 2023/2/14 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ExcelEntity {

    @ExcelProperty(index = 0)
    private String index0;
    @ExcelProperty(index = 1)
    private String index1;
    @ExcelProperty(index = 2)
    private String index2;
    @ExcelProperty(index = 3)
    private String index3;
    @ExcelProperty(index = 4)
    private String index4;
    @ExcelProperty(index = 5)
    private String index5;
    @ExcelProperty(index = 6)
    private String index6;
    @ExcelProperty(index = 7)
    private String index7;
    @ExcelProperty(index = 8)
    private String index8;
    @ExcelProperty(index = 9)
    private String index9;
    @ExcelProperty(index = 10)
    private String index10;
    @ExcelProperty(index = 11)
    private String index11;
    @ExcelProperty(index = 12)
    private String index12;
    @ExcelProperty(index = 13)
    private String index13;
    @ExcelProperty(index = 14)
    private String index14;
    @ExcelProperty(index = 15)
    private String index15;
    @ExcelProperty(index = 16)
    private String index16;
    @ExcelProperty(index = 17)
    private String index17;
    @ExcelProperty(index = 18)
    private String index18;
    @ExcelProperty(index = 19)
    private String index19;
    @ExcelProperty(index = 20)
    private String index20;

    public String getColumnValue(int index) {
        String res = null;
        String paramName = "index" + index;
        try {
            Field[] fields = (Field[]) LocalCache.getCache(ExcelEntity.class.getSimpleName());
            if (fields == null || fields.length == 0) {
                fields = this.getClass().getDeclaredFields();
                LocalCache.addCache(ExcelEntity.class.getSimpleName(), fields);
            }
            for (Field field : fields) {
                if (field.getName().equals(paramName)) {
                    res = (String) field.get(this);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return res;
    }
}
