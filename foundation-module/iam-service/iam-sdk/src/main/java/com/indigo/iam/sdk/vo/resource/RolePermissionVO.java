package com.indigo.iam.sdk.vo.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/29 10:29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RolePermissionVO implements Serializable {

    private List<String> systemIds;

    private List<String> menuIds;

    private List<String> resourceIds;
}
