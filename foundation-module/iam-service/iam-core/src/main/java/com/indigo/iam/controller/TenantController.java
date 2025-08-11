package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.databases.dto.PageResult;
import com.indigo.iam.api.model.dto.TenantQueryDTO;
import com.indigo.iam.api.model.dto.TenantsPageDTO;
import com.indigo.iam.api.model.pojo.IamTenant;
import com.indigo.iam.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户控制器
 *
 * @author 史偕成
 * @date 2025/07/28 06:09
 **/
@RestController
@RequestMapping("/tenants")
@Slf4j
public class TenantController {

    @Autowired
    private TenantService tenantService;

    // ==================== 基础CRUD接口 ====================

    /**
     * 创建租户
     */
    @PostMapping("/createTenant")
    public Result<Boolean> createTenant(@RequestBody IamTenant tenant) {
        boolean result = tenantService.createTenant(tenant);
        return Result.success(result);
    }

    /**
     * 根据ID获取租户
     */
    @GetMapping("/{id}")
    public Result<IamTenant> getTenantById(@PathVariable String id) {
        IamTenant tenant = tenantService.getTenantById(id);
        return Result.success(tenant);
    }

    /**
     * 更新租户
     */
    @PutMapping
    public Result<Boolean> updateTenant(@RequestBody IamTenant tenant) {
        boolean result = tenantService.updateTenant(tenant);
        return Result.success(result);
    }

    /**
     * 删除租户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTenant(@PathVariable String id) {
        boolean result = tenantService.deleteTenant(id);
        return Result.success(result);
    }

    // ==================== 批量操作接口 ====================

    /**
     * 批量创建租户
     */
    @PostMapping("/batch")
    public Result<Boolean> createTenants(@RequestBody List<IamTenant> tenants) {
        boolean result = tenantService.saveBatch(tenants);
        return Result.success(result);
    }

    /**
     * 批量更新租户
     */
    @PutMapping("/batch")
    public Result<Boolean> updateTenants(@RequestBody List<IamTenant> tenants) {
        boolean result = tenantService.updateBatchById(tenants);
        return Result.success(result);
    }

    /**
     * 批量删除租户
     */
    @DeleteMapping("/batch")
    public Result<Boolean> deleteTenants(@RequestBody List<String> ids) {
        boolean result = tenantService.removeByIds(ids);
        return Result.success(result);
    }

    // ==================== 查询接口 ====================

    /**
     * 根据条件查询租户列表（不分页）
     */
    @PostMapping("/list")
    public Result<List<IamTenant>> findTenants(@RequestBody TenantQueryDTO query) {
        List<IamTenant> tenants = tenantService.findTenants(query);
        return Result.success(tenants);
    }

    /**
     * 根据条件分页查询租户
     */
//    @PostMapping("/page")
//    public Result<PageResult<IamTenant>> findTenantsWithPage(@RequestBody TenantQueryDTO query) {
//        PageResult<IamTenant> result = tenantService.findTenantsWithPage(query);
//        return Result.success(result);
//    }

    /**
     * 根据状态查询租户列表
     */
//    @GetMapping("/status/{status}")
//    public Result<List<IamTenant>> getTenantsByStatus(@PathVariable Integer status) {
//        List<IamTenant> tenants = tenantService.getTenantsByStatus(status);
//        return Result.success(tenants);
//    }
//
//    /**
//     * 关键词搜索租户
//     */
//    @GetMapping("/search")
//    public Result<List<IamTenant>> searchTenants(@RequestParam String keyword) {
//        List<IamTenant> tenants = tenantService.searchTenantsByKeyword(keyword);
//        return Result.success(tenants);
//    }

    // ==================== 复杂查询接口 ====================

    /**
     * 查询租户及其创建者信息
     */
    @GetMapping("/with-creator/{status}")
    public Result<List<IamTenant>> getTenantsWithCreator(@PathVariable String status) {
        List<IamTenant> tenants = tenantService.getTenantsWithCreator(status);
        return Result.success(tenants);
    }

    /**
     * 分页查询租户及其创建者信息
     */
    @GetMapping("/with-creator-page/{status}")
    public Result<List<IamTenant>> getTenantsWithCreatorPage(@PathVariable String status) {
        List<IamTenant> tenants = tenantService.getTenantsWithCreatorPage(status);
        return Result.success(tenants);
    }

    /**
     * 统计租户数量
     */
    @GetMapping("/count/{status}")
    public Result<Long> countTenantsWithCreator(@PathVariable String status) {
        Long count = tenantService.countTenantsWithCreator(status);
        return Result.success(count);
    }

    // ==================== 业务接口 ====================

    /**
     * 获取活跃租户
     */
//    @GetMapping("/active")
//    public Result<List<IamTenant>> getActiveTenants() {
//        List<IamTenant> tenants = tenantService.getActiveTenants();
//        return Result.success(tenants);
//    }

    /**
     * 检查租户代码是否存在
     */
//    @GetMapping("/exists/{code}")
//    public Result<Boolean> existsByCode(@PathVariable String code) {
//        boolean exists = tenantService.existsByCode(code);
//        return Result.success(exists);
//    }

    // ==================== 兼容性接口（保持向后兼容） ====================

    /**
     * 分页查询租户（兼容旧接口）
     */
//    @PostMapping("/page-old")
//    public Result<PageResult<IamTenant>> getTenantsPage(@RequestBody TenantsPageDTO request) {
//        PageResult<IamTenant> result = tenantService.getTenantsPage(request);
//        return Result.success(result);
//    }

    // ==================== 查询条件对象示例接口 ====================

//    /**
//     * 使用查询条件对象进行复杂查询
//     */
//    @GetMapping("/condition-example")
//    public Result<List<IamTenant>> getTenantsWithCondition() {
//        List<IamTenant> tenants = tenantService.findTenantsWithCondition();
//        return Result.success(tenants);
//    }
//
//    /**
//     * 使用查询条件对象进行时间范围查询
//     */
//    @GetMapping("/time-range")
//    public Result<List<IamTenant>> getTenantsByTimeRange(
//            @RequestParam("start") String start,
//            @RequestParam("end") String end) {
//        LocalDateTime startTime = LocalDateTime.parse(start);
//        LocalDateTime endTime = LocalDateTime.parse(end);
//        List<IamTenant> tenants = tenantService.findTenantsByTimeRange(startTime, endTime);
//        return Result.success(tenants);
//    }
}
