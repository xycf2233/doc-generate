package com.xycf.generate.common.base;

import com.xycf.generate.config.exception.AppException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: ztc
 * @date: 2022/6/24
 */
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AppException.class)
    public BaseResponse<?> appExceptionHandler(AppException e){
        return BaseResponse.result(e.getCode(),e.getMessage(),null);
    }
}
