//package com.indigo.iam.repository.mapper;
//
//import org.apache.ibatis.annotations.Mapper;
//
//import java.util.List;
//
///**
// * @author 史偕成
// * @date 2025/07/28 06:09
// **/
//@Mapper
//public interface TenantMapper extends BaseMapper<IamTenant> {
//
//    // /**
//    //  * 查询租户及其创建者信息
//    //  */
//    // @Select("""
//    //          SELECT t.*, u.username as creator_name\s
//    //                 FROM tenants t\s
//    //                 LEFT JOIN users u ON t.create_user = u.id\s
//    //                 WHERE t.status = #{status}\
//    //        \s""")
//    // List<IamTenant> findTenantsWithCreator(@Param("status") String status);
//
//    // /**
//    //  * 分页查询租户及其创建者信息
//    //  */
//    // @Select("""
//    //     SELECT t.*, u.username as creator_name
//    //     FROM tenants t
//    //     LEFT JOIN users u ON t.create_user = u.id
//    //     WHERE t.status = #{status}
//    //     ORDER BY t.create_time DESC
//    //     LIMIT #{pageSize} OFFSET #{offset}
//    //     """)
//    // List<IamTenant> findTenantsWithCreatorPage(
//    //     @Param("status") String status,
//    //     @Param("pageSize") Integer pageSize,
//    //     @Param("offset") Integer offset
//    // );
//
//    // /**
//    //  * 统计租户数量
//    //  */
//    // @Select("""
//    //     SELECT COUNT(*)
//    //     FROM tenants t
//    //     LEFT JOIN users u ON t.create_user = u.id
//    //     WHERE t.status = #{status}
//    //     """)
//    // Long countTenantsWithCreator(@Param("status") String status);
//}
