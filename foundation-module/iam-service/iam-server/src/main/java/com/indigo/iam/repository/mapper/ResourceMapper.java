package com.indigo.iam.repository.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.IamResource;
import com.indigo.iam.sdk.dto.query.ResourceDTO;
import com.indigo.iam.sdk.vo.resource.ResourceDetailVO;
import com.indigo.iam.sdk.vo.resource.ResourceVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
public interface ResourceMapper extends EnhancedVoMapper<IamResource, ResourceVO> {

    /**
     * 查询用户拥有的资源列表
     * 通过用户 -> 用户角色 -> 角色资源 -> 资源 关联查询
     * 注意：资源必须关联菜单（menu_id 不为空）
     *
     * @param userId 用户ID
     * @return 资源VO列表
     */
    @Select("""
        SELECT DISTINCT
            res.id,
            res.menu_id,
            res.code,
            res.name,
            res.type,
            res.description,
            res.permissions,
            res.create_time,
            res.modify_time
        FROM iam_user_role ur
        INNER JOIN iam_role_resource rr ON ur.role_id = rr.role_id
        INNER JOIN iam_resources res ON rr.resource_id = res.id
        WHERE ur.user_id = #{userId}
          AND res.deleted = 1
          AND res.menu_id IS NOT NULL
          AND res.menu_id != ''
        """)
    List<ResourceVO> selectUserResources(@Param("userId") String userId);

    /**
     * 资源详情分页查询（多表联查）
     * 关联查询菜单表和系统表，获取菜单的 name 和 code，以及系统的 name 和 code
     * 使用 MyBatis-Plus 的 ew 参数进行 SQL 拼接
     * 
     * @param page 分页对象
     * @param ew 查询条件包装器
     * @return 资源详情分页结果
     */
    @Select("""
        SELECT 
            res.id,
            res.menu_id AS menuId,
            res.code,
            res.name,
            res.type,
            res.description,
            res.permissions,
            res.create_time AS createTime,
            res.modify_time AS modifyTime,
            menu.code AS menuCode,
            menu.name AS menuName,
            sys.code AS systemCode,
            sys.name AS systemName
        FROM iam_resources res
        LEFT JOIN iam_menu menu ON res.menu_id = menu.id
        LEFT JOIN iam_system sys ON menu.system_id = sys.id 
        ${ew.customSqlSegment}
        """)
    Page<ResourceDetailVO> selectResourceDetailPage(Page<ResourceDetailVO> page, @Param("ew") QueryWrapper<IamResource> ew);
}

