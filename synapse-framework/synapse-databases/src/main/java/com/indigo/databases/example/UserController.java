//package com.indigo.databases.example;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.indigo.core.entity.Result;
//import com.indigo.databases.dto.PageDTO;
//import com.indigo.databases.dto.PageResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 用户Controller示例
// * 展示如何在Controller中使用注解SQL + MyBatis-Plus
// *
// * @author 史偕成
// * @date 2024/12/19
// */
//@RestController
//@RequestMapping("/users")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    // ========== MyBatis-Plus基础操作 ==========
//
//    /**
//     * 创建用户 - 使用MyBatis-Plus的save方法
//     */
//    @PostMapping
//    public Result<User> createUser(@RequestBody User user) {
//        userService.save(user);
//        return Result.success(user);
//    }
//
//    /**
//     * 更新用户 - 使用MyBatis-Plus的updateById方法
//     */
//    @PutMapping("/{id}")
//    public Result<Boolean> updateUser(@PathVariable Long id, @RequestBody User user) {
//        user.setId(id);
//        boolean success = userService.updateById(user);
//        return Result.success(success);
//    }
//
//    /**
//     * 删除用户 - 使用MyBatis-Plus的removeById方法
//     */
//    @DeleteMapping("/{id}")
//    public Result<Boolean> deleteUser(@PathVariable Long id) {
//        boolean success = userService.removeById(id);
//        return Result.success(success);
//    }
//
//    /**
//     * 批量删除 - 使用MyBatis-Plus的removeByIds方法
//     */
//    @DeleteMapping("/batch")
//    public Result<Boolean> deleteUsers(@RequestBody List<Long> ids) {
//        boolean success = userService.removeByIds(ids);
//        return Result.success(success);
//    }
//
//    /**
//     * 分页查询 - 使用MyBatis-Plus的page方法
//     */
//    @GetMapping("/page")
//    public Result<IPage<User>> pageUsers(
//            @RequestParam(defaultValue = "1") long current,
//            @RequestParam(defaultValue = "10") long size) {
//        Page<User> page = new Page<>(current, size);
//        IPage<User> result = userService.page(page);
//        return Result.success(result);
//    }
//
//    /**
//     * 列表查询 - 使用MyBatis-Plus的list方法
//     */
//    @GetMapping("/list")
//    public Result<List<User>> listUsers() {
//        List<User> users = userService.list();
//        return Result.success(users);
//    }
//
//    // ========== 注解SQL业务操作 ==========
//
//    /**
//     * 根据ID查询用户详情 - 使用注解SQL
//     */
//    @GetMapping("/{id}")
//    public Result<UserDTO> getUserById(@PathVariable Long id) {
//        UserDTO user = userService.findById(id);
//        return Result.success(user);
//    }
//
//    /**
//     * 根据租户分页查询 - 使用注解SQL
//     */
//    @GetMapping("/tenant/{tenantId}/page")
//    public Result<PageResult<UserDTO>> getUsersByTenant(
//            @PathVariable Long tenantId,
//            @RequestParam(defaultValue = "1") int pageNum,
//            @RequestParam(defaultValue = "10") int pageSize) {
//
//        PageDTO pageDTO = new PageDTO();
//        pageDTO.setPageNum(pageNum);
//        pageDTO.setPageSize(pageSize);
//
//        PageResult<UserDTO> result = userService.findPageByTenant(tenantId, pageDTO);
//        return Result.success(result);
//    }
//
//    /**
//     * 获取用户完整信息 - 使用注解SQL
//     */
//    @GetMapping("/{id}/full")
//    public Result<UserFullDTO> getUserFull(@PathVariable Long id) {
//        UserFullDTO user = userService.getUserFull(id);
//        return Result.success(user);
//    }
//
//    /**
//     * 获取用户统计信息 - 使用注解SQL
//     */
//    @GetMapping("/tenant/{tenantId}/statistics")
//    public Result<UserStatisticsDTO> getUserStatistics(@PathVariable Long tenantId) {
//        UserStatisticsDTO statistics = userService.getUserStatistics(tenantId);
//        return Result.success(statistics);
//    }
//
//    /**
//     * 搜索用户 - 使用注解SQL
//     */
//    @GetMapping("/tenant/{tenantId}/search")
//    public Result<List<UserWithRoleDTO>> searchUsers(
//            @PathVariable Long tenantId,
//            @RequestParam String keyword) {
//        List<UserWithRoleDTO> users = userService.searchUsers(tenantId, keyword);
//        return Result.success(users);
//    }
//
//    // ========== 混合操作示例 ==========
//
//    /**
//     * 创建用户并返回完整信息
//     */
//    @PostMapping("/create-with-full")
//    public Result<UserFullDTO> createUserWithFull(@RequestBody User user) {
//        // 1. 使用MyBatis-Plus保存用户
//        userService.save(user);
//
//        // 2. 使用注解SQL查询完整信息
//        UserFullDTO fullUser = userService.getUserFull(user.getId());
//
//        return Result.success(fullUser);
//    }
//
//    /**
//     * 批量操作示例
//     */
//    @PostMapping("/batch-operations")
//    public Result<String> batchOperations(@RequestBody List<User> users) {
//        // 1. 使用MyBatis-Plus批量保存
//        userService.saveBatch(users);
//
//        // 2. 使用MyBatis-Plus查询
//        List<User> savedUsers = userService.list();
//
//        // 3. 使用注解SQL统计
//        UserStatisticsDTO statistics = userService.getUserStatistics(1L);
//
//        return Result.success("批量操作完成，总用户数：" + statistics.getTotalUsers());
//    }
//}