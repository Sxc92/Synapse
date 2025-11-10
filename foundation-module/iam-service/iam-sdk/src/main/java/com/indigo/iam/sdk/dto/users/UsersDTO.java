package com.indigo.iam.sdk.dto.users;

import com.indigo.core.annotation.QueryCondition;
import com.indigo.core.entity.dto.PageDTO;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 用户查询DTO
 * 
 * <p>继承 {@link PageDTO} 获得分页功能。
 * 如果不需要分页，分页字段有默认值（pageNo=1, pageSize=10），不影响查询结果。</p>
 * 
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
public class UsersDTO extends PageDTO<String> {

    @QueryCondition(field = "account", type = QueryCondition.QueryType.LIKE)
    private String account;

    @QueryCondition(field = "type", type = QueryCondition.QueryType.EQ)
    private String type;

    @QueryCondition(field = "enabled", type = QueryCondition.QueryType.EQ)
    private Boolean enabled;

    @QueryCondition(field = "locked", type = QueryCondition.QueryType.EQ)
    private Boolean locked;

    @QueryCondition(field = "expired", type = QueryCondition.QueryType.EQ)
    private Boolean expired;

    @QueryCondition(field = "real_name", type = QueryCondition.QueryType.LIKE)
    private String realName;

    @QueryCondition(field = "email", type = QueryCondition.QueryType.LIKE)
    private String email;

    @QueryCondition(field = "mobile", type = QueryCondition.QueryType.LIKE)
    private String mobile;
}
