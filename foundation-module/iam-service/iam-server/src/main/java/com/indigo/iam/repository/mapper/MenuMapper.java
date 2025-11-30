package com.indigo.iam.repository.mapper;

import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/08 17:12
 **/
public interface MenuMapper extends EnhancedVoMapper<Menu, MenuVO> {

    /**
     * 查询用户拥有的菜单列表
     * 通过用户 -> 用户角色 -> 角色菜单 -> 菜单 关联查询
     *
     * @param userId 用户ID
     * @return 菜单VO列表
     */
    @Select("""
        SELECT DISTINCT
            m.id,
            m.system_id,
            m.parent_id,
            m.code,
            m.name,
            m.icon,
            m.router,
            m.component,
            m.status,
            m.visible,
            m.create_time,
            m.modify_time
        FROM iam_user_role ur
        INNER JOIN iam_role_menu rm ON ur.role_id = rm.role_id
        INNER JOIN iam_menu m ON rm.menu_id = m.id
        WHERE ur.user_id = #{userId}
          AND m.deleted = 1
          AND m.status = 1
          AND m.visible = 1
        """)
    List<MenuVO> selectUserMenus(@Param("userId") String userId);
}
