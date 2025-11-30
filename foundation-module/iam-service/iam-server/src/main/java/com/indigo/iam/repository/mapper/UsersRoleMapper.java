package com.indigo.iam.repository.mapper;

import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.UsersRole;
import com.indigo.iam.sdk.vo.users.UserRoleVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/06 16:52
 **/
public interface UsersRoleMapper extends EnhancedVoMapper<UsersRole, UserRoleVO> {
    
    /**
     * 查询用户角色列表（多表联查）
     * 关联查询角色表，获取角色编码、描述和状态
     * 
     * @param userId 用户ID
     * @return 用户角色VO列表
     */
    @Select("""
        SELECT 
            ur.user_id AS userId,
            ur.role_id AS roleId,
            r.code AS roleCode,
            r.description AS roleDesc,
            r.status AS roleStatus
        FROM iam_user_role ur
        LEFT JOIN iam_role r ON ur.role_id = r.id
        WHERE ur.user_id = #{userId}
          AND r.deleted = 1
        ORDER BY r.create_time DESC
        """)
    List<UserRoleVO> selectUserRoles(@Param("userId") String userId);
}
