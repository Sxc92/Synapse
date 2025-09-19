package com.indigo.iam.api.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 史偕成
 * @date 2025/08/15 09:22
 **/
@Data
public class LoginDTO implements Serializable {

    private String username;

    private String password;

    private String tenantId;
}
