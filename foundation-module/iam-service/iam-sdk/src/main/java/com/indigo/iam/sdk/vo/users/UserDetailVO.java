package com.indigo.iam.sdk.vo.users;

import com.indigo.core.entity.vo.BaseVO;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/06 16:54
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserDetailVO extends BaseVO<String> {

    private String userId;

    private String account;

    private String password;

    private List<RoleDetailVO> roles;
}
