package com.indigo.iam.sdk.dto.users;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/07 16:22
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class EmpowerDTO extends BaseDTO<String> {

    /**
     * 角色ID集合
     */
    private List<String> roleIds;
}
