package com.xycf.generate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author ztc
 * @Description 类信息
 * @Date 2023/2/3 11:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassEntry {

    /**
     * 类名称
     */
    private String modelClassName;

    /**
     * 类注释
     */
    private String modelCommentText;

    /**
     * 字段
     */
    private List<FieldEntry> fieldEntryList;

    /**
     * 是否必须
     */
    private String must;

    /**
     * 请求格式  query/path/body/form
     */
    private String remarks;

}
