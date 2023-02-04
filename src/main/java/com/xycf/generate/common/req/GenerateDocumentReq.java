package com.xycf.generate.common.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Administrator
 * @description 生成文档入参
 * @date 2023/2/4 19:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateDocumentReq {
    /**
     * 唯一标识
     */
    private String key;
    /**
     * 控制层文件路径
     */
    private List<String> controllerDirs;
    /**
     * 实体层文件路径
     */
    @NotNull
    private List<String> entityDirs;
}
