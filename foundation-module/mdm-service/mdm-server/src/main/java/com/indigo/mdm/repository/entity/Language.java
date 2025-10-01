package com.indigo.mdm.repository.entity;

import com.indigo.databases.entity.AuditEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author 史偕成
 * @date 2025/09/30 07:50
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Language extends AuditEntity<String> {

    private String code;

    private String name;

    private Boolean enabled;

    private Boolean sysDefault;
}
