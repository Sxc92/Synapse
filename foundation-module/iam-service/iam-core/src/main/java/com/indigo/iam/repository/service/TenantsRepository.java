package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
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
    
    // ==================== 业务方法示例 ====================
    
//    /**
//     * 查询租户及其创建者信息
//     */
//
//    default List<IamTenant> findTenantsWithCreator(String status) {
//        return this.getMapper().findTenantsWithCreator(status);
//    }
//
//    /**
//     * 分页查询租户及其创建者信息
//     */
//    default List<IamTenant> findTenantsWithCreatorPage(String status) {
//        return this.getMapper().findTenantsWithCreatorPage(status, 10, 0);
//    }
//
//    /**
//     * 统计租户数量
//     */
//    default Long countTenantsWithCreator(String status) {
//        return this.getMapper().countTenantsWithCreator(status);
//    }
} 