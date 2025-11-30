package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.iam.repository.entity.RoleResource;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
public interface RoleResourceMapper extends BaseMapper<RoleResource> {


    @Select("""
            select resource_id from iam_role_resource where role_id = #{roleId}
            """)
    List<String> listRoleResources(@Param("roleId") String roleId);
}

