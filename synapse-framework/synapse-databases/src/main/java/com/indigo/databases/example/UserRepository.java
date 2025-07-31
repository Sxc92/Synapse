//package com.indigo.databases.example;
//
//import com.indigo.databases.annotation.AutoRepository;
//import com.indigo.databases.annotation.Param;
//import com.indigo.databases.annotation.SqlPage;
//import com.indigo.databases.annotation.SqlQuery;
//import com.indigo.databases.dto.PageDTO;
//import com.indigo.databases.dto.PageResult;
//import com.indigo.databases.repository.BaseRepository;
//
//import java.util.List;
//
///**
// * 用户Repository示例
// * 展示如何使用注解SQL + MyBatis-Plus
// *
// * @author 史偕成
// * @date 2024/12/19
// */
//@AutoRepository
//public interface UserRepository extends BaseRepository<User, UserMapper> {
//
//    // 继承MyBatis-Plus的所有方法：
//    // save(), update(), remove(), list(), page()等
//
//    // 自定义SQL查询
//    @SqlQuery("SELECT * FROM iam_user WHERE username = #{username}")
//    User findByUsername(@Param("username") String username);
//
//    // 条件查询
//    @SqlQuery("SELECT * FROM iam_user WHERE tenant_id = #{tenantId} AND status = #{status}")
//    List<User> findByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") Integer status);
//
//    // 多表关联查询
//    @SqlQuery("""
//        SELECT u.*, r.role_name, r.role_code
//        FROM iam_user u
//        LEFT JOIN iam_user_role ur ON u.id = ur.user_id
//        LEFT JOIN iam_role r ON ur.role_id = r.id
//        WHERE u.id = #{userId}
//    """)
//    UserWithRoleDTO findUserWithRoles(@Param("userId") Long userId);
//
//    // 分页查询
//    @SqlPage(
//        countSql = "SELECT COUNT(*) FROM iam_user WHERE tenant_id = #{tenantId}",
//        dataSql = "SELECT * FROM iam_user WHERE tenant_id = #{tenantId} ORDER BY create_time DESC LIMIT #{pageSize} OFFSET #{pageNum}"
//    )
//    PageResult<User> findPageByTenant(@Param("tenantId") Long tenantId, @PageParam PageDTO pageDTO);
//
//    // 复杂业务查询
//    @SqlQuery("""
//        SELECT u.*, COUNT(r.id) as role_count
//        FROM iam_user u
//        LEFT JOIN iam_user_role ur ON u.id = ur.user_id
//        LEFT JOIN iam_role r ON ur.role_id = r.id
//        WHERE u.tenant_id = #{tenantId}
//        GROUP BY u.id
//        HAVING role_count > #{minRoleCount}
//    """)
//    List<UserWithRoleCountDTO> findUsersWithRoleCount(
//        @Param("tenantId") Long tenantId,
//        @Param("minRoleCount") Integer minRoleCount
//    );
//}
//
//// 示例实体类
//class User {
//    private Long id;
//    private String username;
//    private String email;
//    private Long tenantId;
//    private Integer status;
//    // getter/setter
//}
//
//// 示例DTO类
//class UserWithRoleDTO {
//    private Long id;
//    private String username;
//    private String email;
//    private String roleName;
//    private String roleCode;
//    // getter/setter
//}
//
//class UserWithRoleCountDTO {
//    private Long id;
//    private String username;
//    private String email;
//    private Integer roleCount;
//    // getter/setter
//}
//
//// 示例Mapper接口
//interface UserMapper extends com.baomidou.mybatisplus.core.mapper.BaseMapper<User> {
//}