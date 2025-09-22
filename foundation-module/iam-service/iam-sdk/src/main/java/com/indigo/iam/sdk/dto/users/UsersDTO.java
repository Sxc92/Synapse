package com.indigo.iam.sdk.dto.users;

import com.indigo.core.entity.dto.QueryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 史偕成
 * @title
 * @description
 * @create 2025-09-22 10:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UsersDTO extends QueryDTO {

    private String username;

    private String password;
}
