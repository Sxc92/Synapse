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
 * @date 2025/11/07 15:40
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RoleVO extends BaseVO<String> {

    private String code;

    private String description;

    private Boolean status;
}
