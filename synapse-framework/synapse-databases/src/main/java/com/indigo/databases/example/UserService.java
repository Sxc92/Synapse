//package com.indigo.databases.example;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.indigo.core.entity.Result;
//import com.indigo.databases.annotation.AutoService;
//import com.indigo.databases.annotation.Param;
//import com.indigo.databases.annotation.SqlPage;
//import com.indigo.databases.annotation.SqlQuery;
//import com.indigo.databases.dto.PageDTO;
//import com.indigo.databases.dto.PageResult;
//
//import java.util.List;
//
///**
// * 用户Service示例
// * 展示如何在Service层使用注解SQL + MyBatis-Plus
// *
// * @author 史偕成
// * @date 2024/12/19
// */
//@AutoService
//public interface UserService extends IService<User> {
//
//    // 继承MyBatis-Plus的所有方法：
//    // save(), update(), remove(), list(), page()等
//
//    // 业务方法 - 使用注解SQL
//    @SqlQuery("SELECT * FROM iam_user WHERE id = #{id}")
//    UserDTO findById(@Param("id") Long id);
//
//    // 业务分页查询
//    @SqlPage(
//        countSql = "SELECT COUNT(*) FROM iam_user WHERE tenant_id = #{tenantId}",
//        dataSql = "SELECT * FROM iam_user WHERE tenant_id = #{tenantId} ORDER BY create_time DESC LIMIT #{pageSize} OFFSET #{pageNum}"
//    )
//    PageResult<UserDTO> findPageByTenant(@Param("tenantId") Long tenantId, @PageParam PageDTO pageDTO);
//
//    // 复杂业务聚合查询
//    @SqlQuery("""
//        SELECT u.*,
//               JSON_ARRAYAGG(JSON_OBJECT('id', r.id, 'name', r.role_name)) as roles,
//               JSON_ARRAYAGG(JSON_OBJECT('id', p.id, 'name', p.permission_name)) as permissions
//        FROM iam_user u
//        LEFT JOIN iam_user_role ur ON u.id = ur.user_id
//        LEFT JOIN iam_role r ON ur.role_id = r.id
//        LEFT JOIN iam_role_permission rp ON r.id = rp.role_id
//        LEFT JOIN iam_permission p ON rp.permission_id = p.id
//        WHERE u.id = #{userId}
//        GROUP BY u.id
//    """)
//    UserFullDTO getUserFull(@Param("userId") Long userId);
//
//    // 业务统计查询
//    @SqlQuery("""
//        SELECT
//            COUNT(*) as total_users,
//            COUNT(CASE WHEN status = 1 THEN 1 END) as active_users,
//            COUNT(CASE WHEN status = 0 THEN 1 END) as inactive_users,
//            AVG(permission_count) as avg_permissions
//        FROM (
//            SELECT u.id, u.status, COUNT(p.id) as permission_count
//            FROM iam_user u
//            LEFT JOIN iam_user_role ur ON u.id = ur.user_id
//            LEFT JOIN iam_role_permission rp ON ur.role_id = rp.role_id
//            LEFT JOIN iam_permission p ON rp.permission_id = p.id
//            WHERE u.tenant_id = #{tenantId}
//            GROUP BY u.id, u.status
//        ) user_stats
//    """)
//    UserStatisticsDTO getUserStatistics(@Param("tenantId") Long tenantId);
//
//    // 用户搜索
//    @SqlQuery("""
//        SELECT u.*, r.role_name
//        FROM iam_user u
//        LEFT JOIN iam_user_role ur ON u.id = ur.user_id
//        LEFT JOIN iam_role r ON ur.role_id = r.id
//        WHERE u.tenant_id = #{tenantId}
//        AND (u.username LIKE #{keyword} OR u.email LIKE #{keyword} OR r.role_name LIKE #{keyword})
//        ORDER BY u.create_time DESC
//    """)
//    List<UserWithRoleDTO> searchUsers(@Param("tenantId") Long tenantId, @Param("keyword") String keyword);
//}
//
//// 示例DTO类
//class UserDTO {
//    private Long id;
//    private String username;
//    private String email;
//    private Long tenantId;
//    private Integer status;
//    // getter/setter
//}
//
//class UserFullDTO {
//    private Long id;
//    private String username;
//    private String email;
//    private String roles; // JSON格式的角色列表
//    private String permissions; // JSON格式的权限列表
//    // getter/setter
//}
//
//class UserStatisticsDTO {
//    private Long totalUsers;
//    private Long activeUsers;
//    private Long inactiveUsers;
//    private Double avgPermissions;
//    // getter/setter
//}
//
//class UserWithRoleDTO {
//    private Long id;
//    private String username;
//    private String email;
//    private String roleName;
//    // getter/setter
//}