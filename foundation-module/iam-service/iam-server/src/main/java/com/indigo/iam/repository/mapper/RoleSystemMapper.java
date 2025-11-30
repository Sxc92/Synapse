package com.indigo.iam.repository.mapper;

/**
 * 角色系统关联 Mapper
 *
 * @author 史偕成
 * @date 2025/11/20
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.iam.repository.entity.RoleSystem;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleSystemMapper extends BaseMapper<RoleSystem> {


    @Select(""" 
            select system_id from iam_role_system where role_id = #{roleId}
            """)
    List<String> listRoleSystems(@Param("roleId") String roleId);
}
