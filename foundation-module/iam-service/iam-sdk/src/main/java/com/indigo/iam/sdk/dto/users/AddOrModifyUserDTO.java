package com.indigo.iam.sdk.dto.users;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/11/07 11:13
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AddOrModifyUserDTO extends BaseDTO<String> {

    private String account;

    private String type;

    private String email;

    private String mobile;

    private String realName;

    private String avatar;

}
