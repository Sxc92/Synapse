package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.SqlQuery;
import com.indigo.databases.annotation.SqlPage;
import com.indigo.databases.annotation.Param;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.api.model.pojo.IamTenant;
import com.indigo.iam.repository.mapper.TenantMapper;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/07/30 14:06
 **/
@AutoRepository
public interface TenantsRepository extends BaseRepository<IamTenant, TenantMapper> {
    
    // ==================== SQL查询示例 ====================
    
    /**
     * 传统SQL查询 - 关联查询
     */
    @SqlQuery("""
        SELECT t.*, u.username as creator_name 
        FROM iam_tenant t 
        LEFT JOIN iam_user u ON t.creator_id = u.id 
        WHERE t.status = #{status}
        """)
    List<IamTenant> findTenantsWithCreator(@Param("status") String status);
    
    /**
     * SQL分页查询 - 多表联查
     */
    @SqlPage(
        countSql = "SELECT COUNT(*) FROM iam_tenant t LEFT JOIN iam_user u ON t.creator_id = u.id WHERE t.status = #{status}",
        dataSql = "SELECT t.*, u.username as creator_name FROM iam_tenant t LEFT JOIN iam_user u ON t.creator_id = u.id WHERE t.status = #{status} ORDER BY t.create_time DESC"
    )
    List<IamTenant> findTenantsWithCreatorPage(@Param("status") String status);
} 