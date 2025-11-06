package com.indigo.iam.service;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.constants.StandardErrorCode;
import com.indigo.core.entity.dto.PageDTO;
import com.indigo.core.entity.dto.QueryDTO;
import com.indigo.core.entity.result.PageResult;
import com.indigo.core.exception.Ex;
import com.indigo.core.exception.SynapseException;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.IUsersService;
import com.indigo.iam.sdk.dto.users.UserConditionDTO;
import com.indigo.iam.sdk.dto.users.UsersDTO;
import com.indigo.iam.sdk.dto.users.UsersPageDTO;
import com.indigo.iam.sdk.vo.users.RoleDetailVO;
import com.indigo.iam.sdk.vo.users.UserDetailVO;
import com.indigo.iam.sdk.vo.users.UserWithRoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 史偕成
 * @date 2025/07/23 07:58
 **/
public interface UserService {

//    Users loadUserByUsername(String key);
//
//    Boolean addUser(UsersDTO param);
//
//    Boolean validatePassword(String username, String password);
//
//    Boolean changePassword(String username, String oldPassword, String newPassword);
//
    /**
     * 查询用户详情（包含角色列表）
     * 
     * @param queryDTO 查询条件
     * @return 用户详情列表（包含角色列表）
     */
    List<UserDetailVO> listUserDetails(QueryDTO<?> queryDTO);
    
    /**
     * 分页查询用户详情（包含角色列表）
     * 
     * @param pageDTO 分页查询条件
     * @return 用户详情分页结果（包含角色列表）
     */
    PageResult<UserDetailVO> pageUserDetails(PageDTO<?> pageDTO);
    
    /**
     * 根据用户ID查询用户详情（包含角色列表）
     * 
     * @param userId 用户ID
     * @return 用户详情（包含角色列表）
     */
    UserDetailVO getUserDetailById(UserConditionDTO  param);

}

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final IUsersService IUsersService;

    @Override
    public List<UserDetailVO> listUserDetails(QueryDTO<?> queryDTO) {
        // 1. 查询用户及其角色（一对多，返回扁平化结果）
        List<UserWithRoleVO> userWithRoleList = IUsersService.listWithVoMapping(queryDTO, UserWithRoleVO.class);
        
        // 2. 组装数据：将扁平化结果转换为 UserDetailVO（包含 List<RoleDetailVO>）
        return assembleUserDetails(userWithRoleList);
    }

    @Override
    public PageResult<UserDetailVO> pageUserDetails(PageDTO<?> pageDTO) {
        // 1. 分页查询用户及其角色（一对多，返回扁平化结果）
        PageResult<UserWithRoleVO> pageResult = IUsersService.pageWithVoMapping(pageDTO, UserWithRoleVO.class);
        
        // 2. 组装数据：将扁平化结果转换为 UserDetailVO（包含 List<RoleDetailVO>）
        List<UserDetailVO> userDetailList = assembleUserDetails(pageResult.getRecords());
        
        // 3. 构建分页结果
        return null;
    }

    @Override
    public UserDetailVO getUserDetailById(UserConditionDTO  param) {
                // 构建查询条件（使用 QueryCondition 注解方式）
//        UserConditionDTO queryDTO = new UserConditionDTO();
        // 注意：由于是多表查询，需要使用表别名
        // 这里直接使用 enhancedQuery 方式更合适
//        QueryDTO<Users> conditionDTO = new QueryDTO<>();
//        conditionDTO.setCondition("u.id = '" + userId + "'");

        // 查询用户详情（使用多表关联查询）
        // 使用 listWithVoMapping 方法，自动根据 @VoMapping 注解进行多表关联查询
        List<UserWithRoleVO> userWithRoleList = IUsersService.listWithVoMapping(param, UserWithRoleVO.class);
        if (userWithRoleList.isEmpty()) {
            return null;
        }

        // 组装数据：将扁平化结果转换为 UserDetailVO（包含 List<RoleDetailVO>）
        return assembleUserDetails(userWithRoleList).get(0);
    }

//    @Override
//    public List<UserDetailVO> listUserDetails(QueryDTO<?> queryDTO) {
//        // 1. 查询用户及其角色（一对多，返回扁平化结果）
//        List<UserWithRoleVO> userWithRoleList = IUsersService.listUsersWithRoles(queryDTO);
//
//        // 2. 组装数据：将扁平化结果转换为 UserDetailVO（包含 List<RoleDetailVO>）
//        return assembleUserDetails(userWithRoleList);
//    }
//
//    @Override
//    public PageResult<UserDetailVO> pageUserDetails(PageDTO<?> pageDTO) {
//        // 1. 分页查询用户及其角色（一对多，返回扁平化结果）
//        PageResult<UserWithRoleVO> pageResult = IUsersService.pageUsersWithRoles(pageDTO);
//
//        // 2. 组装数据：将扁平化结果转换为 UserDetailVO（包含 List<RoleDetailVO>）
//        List<UserDetailVO> userDetailList = assembleUserDetails(pageResult.getRecords());
//
//        // 3. 构建分页结果
//        return PageResult.<UserDetailVO>builder()
//            .records(userDetailList)
//            .total(pageResult.getTotal())
//            .current(pageResult.getCurrent())
//            .size(pageResult.getSize())
//            .pages(pageResult.getPages())
//            .build();
//    }
//
//    @Override
//    public UserDetailVO getUserDetailById(String userId) {
//        // 构建查询条件（使用 QueryCondition 注解方式）
//        UserConditionDTO queryDTO = new UserConditionDTO();
//        // 注意：由于是多表查询，需要使用表别名
//        // 这里直接使用 enhancedQuery 方式更合适
//        QueryDTO<Users> conditionDTO = new QueryDTO<>();
//        conditionDTO.setCondition("u.id = '" + userId + "'");
//
//        // 查询用户详情
//        List<UserDetailVO> userDetailList = listUserDetails(conditionDTO);
//
//        if (userDetailList.isEmpty()) {
//            return null;
//        }
//
//        return userDetailList.get(0);
//    }
//
    /**
     * 组装用户详情数据
     * 将扁平化的 UserWithRoleVO 列表转换为 UserDetailVO 列表（包含角色列表）
     *
     * @param userWithRoleList 扁平化的用户角色关联数据
     * @return 组装后的用户详情列表
     */
    private List<UserDetailVO> assembleUserDetails(List<UserWithRoleVO> userWithRoleList) {
        if (userWithRoleList == null || userWithRoleList.isEmpty()) {
            return Collections.emptyList();
        }

        // 按用户ID分组（处理null值）
        Map<String, List<UserWithRoleVO>> userGroupMap = userWithRoleList.stream()
            .collect(Collectors.groupingBy(
                vo -> vo.getId() != null ? vo.getId() : "null",
                LinkedHashMap::new,  // 保持顺序
                Collectors.toList()
            ));

        // 组装数据
        List<UserDetailVO> userDetailList = new ArrayList<>();
        for (Map.Entry<String, List<UserWithRoleVO>> entry : userGroupMap.entrySet()) {
            List<UserWithRoleVO> userRoleList = entry.getValue();
            if (userRoleList.isEmpty()) {
                continue;
            }

            // 获取第一条记录作为用户基础信息
            UserWithRoleVO firstRecord = userRoleList.get(0);

            // 构建用户详情VO
            UserDetailVO userDetail = UserDetailVO.builder()
                .userId(firstRecord.getId())
                .account(firstRecord.getAccount())
                .password(null)  // 不返回密码
                .build();

            // 设置基础字段（从 BaseVO 继承）
            userDetail.setId(firstRecord.getId());
            userDetail.setCreateTime(firstRecord.getCreateTime());
            userDetail.setModifyTime(firstRecord.getModifyTime());
            userDetail.setCreateUser(firstRecord.getCreateUser());
            userDetail.setModifyUser(firstRecord.getModifyUser());

            // 组装角色列表
            List<RoleDetailVO> roleList = userRoleList.stream()
                .filter(record -> StringUtils.hasText(record.getRoleId()))  // 过滤掉没有角色的记录
                .map(record -> {
                    RoleDetailVO roleDetail = RoleDetailVO.builder()
                        .roleCode(record.getRoleCode())
                        .roleName(record.getRoleCode())  // 如果没有roleName字段，使用roleCode
                        .roleDesc(record.getRoleDesc())
                        .build();

                    // 设置基础字段
                    roleDetail.setId(record.getRoleId());
                    roleDetail.setCreateTime(record.getCreateTime());
                    roleDetail.setModifyTime(record.getModifyTime());

                    return roleDetail;
                })
                .distinct()  // 去重（基于roleId）
                .collect(Collectors.toList());

            userDetail.setRoles(roleList);
            userDetailList.add(userDetail);
        }

        return userDetailList;
    }
//
//    @Override
//    public Users loadUserByUsername(String key) {
//        IUsersService.getOne(new LambdaQueryWrapper<Users>()
//                .eq(Users::getAccount, key));
//        return null;
//    }
//
//    @Override
//    @GlobalTransactional(rollbackFor = {Exception.class, SynapseException.class})
//    public Boolean addUser(UsersDTO param) {
//        // 检查用户名是否已存在
//        Users existingUser = IUsersService.getOne(new LambdaQueryWrapper<Users>()
//                .eq(Users::getAccount, param.getUsername()));
//        if (existingUser != null) {
//            Ex.throwEx(StandardErrorCode.USER_ALREADY_EXISTS);
//        }
//
//        // 创建用户
//        Users users = new Users();
//        users.setAccount(param.getUsername());
//        // 加密密码
//        String encodedPassword = BCrypt.hashpw(param.getPassword(), BCrypt.gensalt());
//        users.setPassword(encodedPassword);
//        // 设置其他字段
//        users.setAccount(param.getUsername());
//        // 保存用户
//        boolean result = IUsersService.save(users);
//        if (result) {
//            log.info("用户创建成功: {}", param.getUsername());
//        } else {
//            log.error("用户创建失败: {}", param.getUsername());
//        }
//        return result;
//    }
//
//    @Override
//    public Boolean validatePassword(String username, String password) {
//        try {
//            Users user = loadUserByUsername(username);
//            if (user == null) {
//                log.warn("用户不存在: {}", username);
//                return false;
//            }
//
//            // 验证密码
//            boolean isValid = BCrypt.checkpw(password, user.getPassword());
//            if (isValid) {
//                log.info("密码验证成功: {}", username);
//            } else {
//                log.warn("密码验证失败: {}", username);
//            }
//
//            return isValid;
//        } catch (Exception e) {
//            log.error("密码验证异常: {}", username, e);
//            return false;
//        }
//    }
//
//    @Override
//    public Boolean changePassword(String username, String oldPassword, String newPassword) {
//        try {
//            // 验证旧密码
//            if (!validatePassword(username, oldPassword)) {
//                log.warn("旧密码验证失败: {}", username);
//                return false;
//            }
//
//            // 检查新密码强度
////            PasswordEncoderUtils.PasswordStrength strength = passwordEncoderUtils.checkPasswordStrength(newPassword);
////            if (strength == PasswordEncoderUtils.PasswordStrength.WEAK) {
////                log.warn("新密码强度太弱: {}", username);
////                return false;
////            }
//
//            // 获取用户
//            Users user = loadUserByUsername(username);
//            if (user == null) {
//                log.warn("用户不存在: {}", username);
//                return false;
//            }
//
//            // 加密新密码
//            String encodedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
//            user.setPassword(encodedNewPassword);
//
//            // 更新用户
//            boolean result = IUsersService.updateById(user);
//            if (result) {
//                log.info("密码修改成功: {}", username);
//            } else {
//                log.error("密码修改失败: {}", username);
//            }
//
//            return result;
//        } catch (Exception e) {
//            log.error("密码修改异常: {}", username, e);
//            return false;
//        }
//    }
}
