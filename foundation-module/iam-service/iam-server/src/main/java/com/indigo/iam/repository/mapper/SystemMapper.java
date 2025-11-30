package com.indigo.iam.repository.mapper;


import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.IamSystem;
import com.indigo.iam.sdk.vo.resource.SystemVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/08 15:40
 **/
public interface SystemMapper extends EnhancedVoMapper<IamSystem, SystemVO> {

    /**
     * 查询用户拥有的系统列表
     * 通过用户 -> 用户角色 -> 角色菜单 -> 菜单 -> 系统 关联查询
     *
     * @param userId 用户ID
     * @return 系统VO列表
     */
    @Select("""
        SELECT DISTINCT
            sys.id,
            sys.code,
            sys.name,
            sys.logo,
            sys.status,
            sys.sorted,
            sys.create_time,
            sys.modify_time
        FROM iam_user_role ur
        INNER JOIN iam_role_menu rm ON ur.role_id = rm.role_id
        INNER JOIN iam_menu m ON rm.menu_id = m.id
        INNER JOIN iam_system sys ON m.system_id = sys.id
        WHERE ur.user_id = #{userId}
          AND m.deleted = 1
          AND m.status = 1
          AND sys.deleted = 1
          AND sys.status = 1
        """)
    List<SystemVO> selectUserSystems(@Param("userId") String userId);
}
