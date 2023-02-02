package com.xycf.generate.config.exception;

import com.xycf.generate.common.base.AbstractExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author: ztc
 * @date: 2022/6/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AppException extends RuntimeException{
    private String code;
    private String message;
    public AppException(AbstractExceptionEnum exceptionEnum){
        this.code = exceptionEnum.getErrorCode();
        this.message = exceptionEnum.getUserTip();
    }
    public AppException(String message){
        this.code = "50000";
        this.message = message;
    }
}
