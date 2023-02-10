package com.xycf.generate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/10 15:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestParam {
    private String name;

    private String type;

    private String must;

    private String defaultValue;

    private String remarks;
}