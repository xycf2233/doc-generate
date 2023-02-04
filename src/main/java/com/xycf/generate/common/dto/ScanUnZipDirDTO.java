package com.xycf.generate.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 扫描解压文件后的到的 控制层map 和 实体层map
 * @date 2023/2/4 18:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanUnZipDirDTO {

    /**
     * 控制层文件map  k：类名  v：文件路径
     */
    Map<String, String> controllerFileMap;
    /**
     * 实体层文件map k：类名 v：文件路径
     */
    Map<String, String> entityFileMap;
}
