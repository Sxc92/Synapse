package com.indigo.core.entity;

import com.indigo.core.exception.enums.ErrorCode;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.indigo.core.exception.enums.ErrorCode.BASE_SUCCESS;

/**
 * @author 史偕成
 * @date 2025/04/24 21:55
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private T data;

    private String msg;

    private String code;

    public static <T> Result<T> success() {
        return Result.<T>builder()
                .data(null)
                .code(BASE_SUCCESS.getCode())
                .msg(BASE_SUCCESS.getMessage())
                .build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .data(data)
                .code(BASE_SUCCESS.getCode())
                .msg(BASE_SUCCESS.getMessage())
                .build();
    }

    public static <T> Result<T> success(T data, int code, String msg) {
        return Result.<T>builder()
                .data(data)
                .code(BASE_SUCCESS.getCode())
                .msg(msg)
                .build();
    }


    public static <T> Result<T> error() {
        return Result.<T>builder()
                .data(null)
                .code(ErrorCode.BASE_ERROR.getCode())
                .msg(ErrorCode.BASE_ERROR.getMessage())
                .build();
    }

    public static <T> Result<T> error(String msg) {
        return Result.<T>builder()
                .data(null)
                .code(ErrorCode.BASE_ERROR.getCode())
                .msg(msg)
                .build();
    }

    public static <T> Result<T> error(String code, String msg) {
        return Result.<T>builder()
                .data(null)
                .code(code)
                .msg(msg)
                .build();
    }

    public Boolean isSuccess() {
        return BASE_SUCCESS.getCode().equals(code);
    }
}
