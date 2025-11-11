package com.indigo.iam.sdk.dto.associated;

import com.indigo.core.entity.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 角色资源关联DTO
 * 
 * @author 史偕成
 * @date 2025/11/10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class RoleResourceDTO extends BaseDTO<String> {

    /**
     * 资源ID集合
     */
    private List<String> resourceIds;
}

