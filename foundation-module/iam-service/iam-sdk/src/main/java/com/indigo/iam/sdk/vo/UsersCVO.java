package com.indigo.iam.sdk.vo;

import com.indigo.core.annotation.FieldMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author 史偕成
 * @title
 * @description
 * @create 2025-09-23 9:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UsersCVO extends UsersVO{

    private String password;

    @FieldMapping(value = "last_login_time")
    private LocalDateTime lastDateTime;
}
