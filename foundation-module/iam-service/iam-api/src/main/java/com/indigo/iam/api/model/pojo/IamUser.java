package com.indigo.iam.api.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.indigo.databases.entity.AuditEntity;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/07/22 15:57
 **/
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("users")
public class IamUser extends AuditEntity<String> {

    private String username;

    private String password;
}
