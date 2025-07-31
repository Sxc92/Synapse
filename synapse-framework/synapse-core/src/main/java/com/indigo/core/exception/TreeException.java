package com.indigo.core.exception;

import lombok.Getter;

/**
 * 树结构相关异常
 *
 * @author 史偕成
 * @date 2025/05/14
 */
@Getter
public class TreeException extends BaseException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ERROR_CODE = "tree.error";

    public TreeException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public TreeException(String message, Throwable cause) {
        super(DEFAULT_ERROR_CODE, message, cause);
    }

    public TreeException(Throwable cause) {
        super(DEFAULT_ERROR_CODE, cause.getMessage(), cause);
    }

    public TreeException(String code, String message) {
        super(code, message);
    }

    public TreeException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
} 