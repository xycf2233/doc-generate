package com.xycf.generate.common.base;

import com.xycf.generate.common.enums.base.BaseResponseEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author ztc
 * @Description 公共返回类
 * @Date 2022/12/13 11:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BaseResponse<T> {

    @ApiModelProperty("响应编码")
    private String code;
    @ApiModelProperty("响应信息")
    private String message;
    @ApiModelProperty("响应数据")
    private T data;

    /**
     * T BaseResponse<T> 标识返回的是一个BaseResponse下的泛型
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T t){
        return BaseResponse.result(BaseResponseEnum.SUCCESS,t);
    }

    public static <T> BaseResponse<T> fail(T t){
        return BaseResponse.result(BaseResponseEnum.FAIL,t);
    }

    public static <T> BaseResponse<T> result(BaseResponseEnum resultCode, T data) {
        return result(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> BaseResponse<T> result(String code, String message, T data){
        return new BaseResponse(code,message,data);
    }

}
