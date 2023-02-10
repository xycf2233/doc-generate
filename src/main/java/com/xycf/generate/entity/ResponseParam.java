package com.xycf.generate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/10 15:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseParam {

    private String name;
    private String type;
    private String remarks;
}
