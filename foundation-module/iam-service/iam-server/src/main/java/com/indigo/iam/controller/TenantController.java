//package com.indigo.iam.controller;
//
//import cn.dev33.satoken.annotation.SaCheckPermission;
//import com.indigo.core.entity.Result;
//import com.indigo.iam.api.model.dto.TenantQueryDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * 租户控制器
// *
// * @author 史偕成
// * @date 2025/07/28 06:09
// **/
//@RestController
//@RequestMapping("/tenants")
//@Slf4j
//@RequiredArgsConstructor
//public class TenantController {
//
//    private final TenantService tenantService;
//
//    private final TenantsRepository tenantsRepository;
//
//    // ==================== 基础CRUD接口 ====================
//
//    /**
//     * 创建租户
//     */
//    @SaCheckPermission(value = {"tenant-add"})
//    @PostMapping("/createTenant")
//    public Result<Boolean> createTenant(@RequestBody IamTenant tenant) {
//        boolean result = tenantService.addOrModifyTenant(tenant);
//        return Result.success(result);
//    }
//
//    /**
//     * 根据ID获取租户
//     */
//    @GetMapping("/{id}")
//    public Result<IamTenant> getTenantById(@PathVariable String id) {
//        IamTenant tenant = tenantService.getTenantById(id);
//        return Result.success(tenant);
//    }
//
//    /**
//     * 删除租户
//     */
//    @DeleteMapping("remove")
//    public Result<Boolean> deleteTenant(@RequestBody List<String> ids) {
//        boolean result = tenantService.deleteTenant(ids);
//        return Result.success(result);
//    }
//
//
//
//    // ==================== 查询接口 ====================
//
//    /**
//     * 根据条件查询租户列表（不分页）
//     */
//    @PostMapping("/list")
//    public Result<List<IamTenant>> findTenants(@RequestBody TenantQueryDTO query) {
//        List<IamTenant> tenants = tenantService.findTenants(query);
//        return Result.success(tenants);
//    }
//
//
//    // ==================== 复杂查询接口 ====================
//
//    /**
//     * 查询租户及其创建者信息
//     */
////    @GetMapping("/with-creator/{status}")
////    public Result<List<IamTenant>> getTenantsWithCreator(@PathVariable String status) {
////        List<IamTenant> tenants = tenantService.getTenantsWithCreator(status);
////        return Result.success(tenants);
////    }
//
//    /**
//     * 分页查询租户及其创建者信息
//     */
////    @PostMapping("/page")
////    public Result<List<IamTenant>> getTenantsWithCreatorPage(@RequestBody TenantQueryDTO query) {
////        List<IamTenant> tenants = tenantService.findTenantsWithPage(status);
////        return Result.success(tenants);
////    }
//
//
//
//}
