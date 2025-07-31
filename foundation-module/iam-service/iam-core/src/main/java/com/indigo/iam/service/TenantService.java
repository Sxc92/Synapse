package com.indigo.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.indigo.databases.dto.PageResult;
import com.indigo.iam.api.model.dto.TenantsPageDTO;
import com.indigo.iam.api.model.pojo.IamTenant;
import com.indigo.iam.repository.service.TenantsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 史偕成
 * @date 2025/07/30 14:06
 **/
@Service
@Slf4j
public class TenantService {

    @Autowired
    private TenantsRepository tenantsRepository;

    // ==================== 基础CRUD操作（直接使用IService方法） ====================

    /**
     * 创建租户
     */
    public boolean createTenant(IamTenant tenant) {
        log.info("创建租户: {}", tenant.getCode());
        return tenantsRepository.save(tenant);
    }

    /**
     * 根据ID查询租户
     */
    public IamTenant getTenantById(Long id) {
        log.info("查询租户: {}", id);
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
    public boolean deleteTenant(Long id) {
        log.info("删除租户: {}", id);
        return tenantsRepository.removeById(id);
    }

    // ==================== 查询操作（直接使用IService方法） ====================



    /**
     * 分页查询租户 - 支持DTO查询条件（推荐使用）
     */
    public PageResult<IamTenant> getTenantsPage(TenantsPageDTO params) {
        log.info("分页查询租户，页码: {}, 查询条件: {}", params.getPageNo(), params);
        return tenantsRepository.pageWithCondition(params);
    }



    /**
     * 根据状态查询租户列表 - 使用自动查询条件构建
     */
    public List<IamTenant> getTenantsByStatus(Integer status) {
        log.info("根据状态查询租户: {}", status);
        IamTenant queryEntity = new IamTenant();
        queryEntity.setStatus(status);
        return tenantsRepository.listWithCondition(queryEntity);
    }

    /**
     * 关键词搜索租户 - 使用自动查询条件构建
     */
    public List<IamTenant> searchTenantsByKeyword(String keyword) {
        log.info("关键词搜索租户: {}", keyword);
        IamTenant queryEntity = new IamTenant();
        queryEntity.setCode(keyword);
        return tenantsRepository.listWithCondition(queryEntity);
    }

    /**
     * 获取租户总数 - 使用IService的count方法
     */
    public Long getTenantCount() {
        log.info("获取租户总数");
        return tenantsRepository.count();
    }

    /**
     * 根据代码查询租户 - 使用IService的getOne方法
     */
    public IamTenant findTenantByCode(String code) {
        log.info("根据代码查询租户: {}", code);
        LambdaQueryWrapper<IamTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamTenant::getCode, code);
        return tenantsRepository.getOne(wrapper);
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
    public boolean removeByIds(List<Long> ids) {
        log.info("批量删除租户，ID数量: {}", ids.size());
        return tenantsRepository.removeByIds(ids);
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
     * 检查租户代码是否存在
     */
    public boolean existsByCode(String code) {
        log.info("检查租户代码是否存在: {}", code);
        IamTenant tenant = findTenantByCode(code);
        return tenant != null;
    }

    // ==================== 多表联查（使用SQL注解） ====================

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
}