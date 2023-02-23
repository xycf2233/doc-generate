package com.xycf.generate.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/23 17:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequest {
    private Integer pageSize;
    private Integer pageNo;
    private String orderBy;
    private String sortBy;

}
