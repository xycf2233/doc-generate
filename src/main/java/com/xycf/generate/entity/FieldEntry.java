package com.xycf.generate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 属性字段对应注释
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldEntry {

    /**
     * 参数名
     */
    private String fieldName;
    /**
     * 类型
     */
    private String fieldType;
    /**
     * 说明
     */
    private String fieldExplain;

    /**
     * 子字段
     */
    private List<FieldEntry> fields;

    /**
     * 是否必须
     */
    private String must;

    /**
     * 默认值
     */
    private String defaultValue;

    public FieldEntry(String fieldName, String fieldType, String fieldExplain) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldExplain = fieldExplain;
    }
}
