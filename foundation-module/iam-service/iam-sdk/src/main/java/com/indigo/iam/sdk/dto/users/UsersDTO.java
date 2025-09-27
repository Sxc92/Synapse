package com.indigo.iam.sdk.dto.users;

import com.indigo.core.entity.dto.QueryDTO;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @title
 * @description
 * @create 2025-09-22 10:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UsersDTO extends QueryDTO<String> {

    private String username;

    private String password;
}
