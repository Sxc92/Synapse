package com.indigo.iam.sdk.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 *
 * @author 史偕成
 * @date 2025/01/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间（秒）
     */
    private Long expiresIn;

}

