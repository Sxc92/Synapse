package com.indigo.iam.service;

import com.indigo.core.entity.Result;
import com.indigo.databases.dto.result.PageResult;
import com.indigo.iam.api.model.dto.TenantQueryDTO;
import com.indigo.iam.api.model.pojo.IamTenant;
import com.indigo.iam.repository.service.TenantsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户服务
 *
 * @author 史偕成
 * @date 2025/07/28 06:09
 **/

public interface TenantService {

    boolean addOrModifyTenant(IamTenant tenant);

    IamTenant getTenantById(String id);

    boolean deleteTenant(List<String> ids);

    List<IamTenant> findTenants(TenantQueryDTO query);

    PageResult<IamTenant> findTenantsWithPage(TenantQueryDTO query);

}

@Service
@Slf4j
@RequiredArgsConstructor
class TenantServiceImpl implements TenantService {

    private final TenantsRepository tenantsRepository;

    // ==================== 基础CRUD（直接使用IService方法） ====================

    /**
     * 创建租户
     */
    public boolean addOrModifyTenant(IamTenant tenant) {
        return tenantsRepository.save(tenant);
    }

    /**
     * 根据ID获取租户
     */
    public IamTenant getTenantById(String id) {
        return tenantsRepository.getById(id);
    }

    /**
     * 更新租户
     */
    public boolean updateTenant(IamTenant tenant) {
        return tenantsRepository.updateById(tenant);
    }

    /**
     * 删除租户
     */
    public boolean deleteTenant(List<String> ids) {
        return tenantsRepository.removeByIds(ids);
    }

    // ==================== 条件查询（使用@QueryCondition） ====================

    /**
     * 根据条件查询租户列表
     */
    public List<IamTenant> findTenants(TenantQueryDTO query) {
        log.info("根据条件查询租户: {}", query);
        return tenantsRepository.listWithDTO(query);
    }

    /**
     * 根据条件分页查询租户
     */
    public PageResult<IamTenant> findTenantsWithPage(TenantQueryDTO query) {
        log.info("根据条件分页查询租户: {}", query);
        return tenantsRepository.pageWithCondition(query);
    }

}