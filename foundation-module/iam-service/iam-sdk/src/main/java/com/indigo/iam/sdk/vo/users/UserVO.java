package com.indigo.iam.sdk.vo.users;

import com.indigo.core.entity.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/07 14:28
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserVO extends BaseVO<String> {

    private String account;

    private String type;

    private Boolean enabled;

    private Boolean locked;

    private Boolean expired;

    private String email;

    private String mobile;

    private String realName;

    private String avatar;
}
