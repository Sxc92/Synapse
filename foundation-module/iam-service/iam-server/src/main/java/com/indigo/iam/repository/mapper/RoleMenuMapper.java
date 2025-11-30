package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indigo.iam.repository.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Select("""
            select menu_id from iam_role_menu where role_id = #{roleId}
            """)
    List<String> listRoleMenu(@Param("roleId") String roleId);
}

