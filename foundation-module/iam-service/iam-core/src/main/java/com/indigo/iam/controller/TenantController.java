package com.indigo.iam.controller;

import com.indigo.databases.dto.PageResult;
import com.indigo.core.entity.Result;
import com.indigo.iam.api.model.dto.TenantsPageDTO;
import com.indigo.iam.api.model.pojo.IamTenant;
import com.indigo.iam.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/07/28 07:09
 **/
@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 创建租户
     */
    @PostMapping("/create")
    public Result<Boolean> createTenant(@RequestBody IamTenant tenant) {
        boolean success = tenantService.createTenant(tenant);
        return Result.success(success);
    }

    /**
     * 根据ID查询租户
     */
    @GetMapping("/{id}")
    public Result<IamTenant> getTenantById(@PathVariable Long id) {
        IamTenant tenant = tenantService.getTenantById(id);
        return Result.success(tenant);
    }

    /**
     * 更新租户
     */
    @PutMapping("/update")
    public Result<Boolean> updateTenant(@RequestBody IamTenant tenant) {
        boolean success = tenantService.updateTenant(tenant);
        return Result.success(success);
    }

    /**
     * 删除租户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTenant(@PathVariable Long id) {
        boolean success = tenantService.deleteTenant(id);
        return Result.success(success);
    }

    /**
     * 根据状态查询租户列表
     */
    @GetMapping("/status/{status}")
    public Result<List<IamTenant>> getTenantsByStatus(@PathVariable Integer status) {
        List<IamTenant> tenants = tenantService.getTenantsByStatus(status);
        return Result.success(tenants);
    }

    /**
     * 分页查询租户 - 支持DTO查询条件
     */
    @PostMapping("/page")
    public Result<PageResult<IamTenant>> getTenantsPage(@RequestBody TenantsPageDTO request) {
        PageResult<IamTenant> result = tenantService.getTenantsPage(request);
        return Result.success(result);
    }

    /**
     * 关键词搜索租户
     */
    @GetMapping("/search")
    public Result<List<IamTenant>> searchTenants(@RequestParam String keyword) {
        List<IamTenant> tenants = tenantService.searchTenantsByKeyword(keyword);
        return Result.success(tenants);
    }

    /**
     * 获取活跃租户
     */
    @GetMapping("/active")
    public Result<List<IamTenant>> getActiveTenants() {
        List<IamTenant> tenants = tenantService.getActiveTenants();
        return Result.success(tenants);
    }

    /**
     * 获取租户总数
     */
    @GetMapping("/count")
    public Result<Long> getTenantCount() {
        Long count = tenantService.getTenantCount();
        return Result.success(count);
    }

    /**
     * 检查租户代码是否存在
     */
    @GetMapping("/exists/{code}")
    public Result<Boolean> existsByCode(@PathVariable String code) {
        boolean exists = tenantService.existsByCode(code);
        return Result.success(exists);
    }

    /**
     * 查询租户及其创建者信息（多表联查）
     */
    @GetMapping("/with-creator/{status}")
    public Result<List<IamTenant>> getTenantsWithCreator(@PathVariable String status) {
        List<IamTenant> tenants = tenantService.getTenantsWithCreator(status);
        return Result.success(tenants);
    }
}
