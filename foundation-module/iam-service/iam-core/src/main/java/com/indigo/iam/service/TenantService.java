package com.indigo.iam.service;

import com.indigo.core.entity.Result;
import com.indigo.core.utils.PageResult;
import com.indigo.iam.api.model.dto.TenantQueryDTO;
import com.indigo.iam.api.model.dto.TenantQueryCondition;
import com.indigo.iam.api.model.dto.TenantsPageDTO;
import com.indigo.iam.api.model.pojo.IamTenant;
import com.indigo.iam.repository.mapper.TenantMapper;
import com.indigo.iam.repository.service.TenantsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户服务
 *
 * @author 史偕成
 * @date 2025/07/28 06:09
 **/
@Service
@Slf4j
public class TenantService {

    @Autowired
    private TenantsRepository tenantsRepository;

    // ==================== 基础CRUD（直接使用IService方法） ====================

    /**
     * 创建租户
     */
    public boolean createTenant(IamTenant tenant) {
        log.info("创建租户: {}", tenant.getCode());
        return tenantsRepository.save(tenant);
    }

    /**
     * 根据ID获取租户
     */
    public IamTenant getTenantById(String id) {
        log.info("根据ID获取租户: {}", id);
        return tenantsRepository.getById(id);
    }

    /**
     * 更新租户
     */
    public boolean updateTenant(IamTenant tenant) {
        log.info("更新租户: {}", tenant.getId());
        return tenantsRepository.updateById(tenant);
    }

    /**
     * 删除租户
     */
    public boolean deleteTenant(String id) {
        log.info("删除租户: {}", id);
        return tenantsRepository.removeById(id);
    }

    // ==================== 批量操作（直接使用IService方法） ====================

    /**
     * 批量保存
     */
    public boolean saveBatch(List<IamTenant> tenants) {
        log.info("批量保存租户，数量: {}", tenants.size());
        return tenantsRepository.saveBatch(tenants);
    }

    /**
     * 批量更新
     */
    public boolean updateBatchById(List<IamTenant> tenants) {
        log.info("批量更新租户，数量: {}", tenants.size());
        return tenantsRepository.updateBatchById(tenants);
    }

    /**
     * 批量删除
     */
    public boolean removeByIds(List<String> ids) {
        log.info("批量删除租户，ID数量: {}", ids.size());
        return tenantsRepository.removeByIds(ids);
    }

    // ==================== 条件查询（使用@QueryCondition） ====================

    /**
     * 根据条件查询租户列表
     */
    public List<IamTenant> findTenants(TenantQueryDTO query) {
        log.info("根据条件查询租户: {}", query);
        return tenantsRepository.listWithCondition(query);
    }

    /**
     * 根据条件分页查询租户
     */
    public PageResult<IamTenant> findTenantsWithPage(TenantQueryDTO query) {
        log.info("根据条件分页查询租户: {}", query);
        return tenantsRepository.pageWithCondition(query);
    }

    /**
     * 根据状态查询租户列表 - 使用Builder模式
     */
    public List<IamTenant> getTenantsByStatus(Integer status) {
        log.info("根据状态查询租户: {}", status);
        // 方式1：使用Builder模式的静态方法
        TenantQueryDTO query = TenantQueryDTO.byStatus(status);
        return tenantsRepository.listWithCondition(query);
    }

    /**
     * 关键词搜索租户 - 使用Builder模式
     */
    public List<IamTenant> searchTenantsByKeyword(String keyword) {
        log.info("关键词搜索租户: {}", keyword);
        // 方式1：使用Builder模式的静态方法
        TenantQueryDTO query = TenantQueryDTO.byKeyword(keyword);
        return tenantsRepository.listWithCondition(query);
    }

    /**
     * 检查租户代码是否存在 - 使用Builder模式
     */
    public boolean existsByCode(String code) {
        log.info("检查租户代码是否存在: {}", code);
        // 方式1：使用Builder模式的静态方法
        TenantQueryDTO query = TenantQueryDTO.byCode(code);
        return tenantsRepository.countWithCondition(query) > 0;
    }

    // ==================== 使用查询条件对象的示例 ====================

    /**
     * 使用查询条件对象进行复杂查询
     */
    public List<IamTenant> findTenantsWithCondition() {
        log.info("使用查询条件对象进行复杂查询");
        // 方式2：使用Lombok Builder的链式调用
        TenantQueryCondition condition = TenantQueryCondition.builder()
            .status(1)
            .description("测试")
            .build();
        return tenantsRepository.listWithCondition(condition);
    }

    /**
     * 使用查询条件对象进行时间范围查询
     */
    public List<IamTenant> findTenantsByTimeRange(LocalDateTime start, LocalDateTime end) {
        log.info("使用查询条件对象进行时间范围查询: {} - {}", start, end);
        // 方式2：使用Lombok Builder
        TenantQueryCondition condition = TenantQueryCondition.builder()
            .createTime(new LocalDateTime[]{start, end})
            .status(1)
            .build();
        
        return tenantsRepository.listWithCondition(condition);
    }

    // ==================== 复杂查询（使用@Select注解） ====================

    /**
     * 查询租户及其创建者信息
     */
    public List<IamTenant> getTenantsWithCreator(String status) {
        log.info("查询租户及其创建者信息，状态: {}", status);
        return tenantsRepository.findTenantsWithCreator(status);
    }

    /**
     * 分页查询租户及其创建者信息
     */
    public List<IamTenant> getTenantsWithCreatorPage(String status) {
        log.info("分页查询租户及其创建者信息，状态: {}", status);
        return tenantsRepository.findTenantsWithCreatorPage(status);
    }

    /**
     * 统计租户数量
     */
    public Long countTenantsWithCreator(String status) {
        log.info("统计租户数量，状态: {}", status);
        return tenantsRepository.countTenantsWithCreator(status);
    }

    // ==================== 业务方法 ====================

    /**
     * 获取活跃租户
     */
    public List<IamTenant> getActiveTenants() {
        log.info("获取活跃租户");
        return getTenantsByStatus(1);
    }

    /**
     * 分页查询租户（兼容旧接口）
     */
    public PageResult<IamTenant> getTenantsPage(TenantsPageDTO params) {
        log.info("分页查询租户: {}", params);
        TenantQueryDTO query = new TenantQueryDTO();
        query.setPageNo(params.getPageNo());
        query.setPageSize(params.getPageSize());
        query.setStatus(params.getStatus());
        query.setOrderByList(params.getOrderByList());
        return tenantsRepository.pageWithCondition(query);
    }
}