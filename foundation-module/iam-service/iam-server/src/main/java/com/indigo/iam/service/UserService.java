package com.indigo.iam.service;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.constants.StandardErrorCode;
import com.indigo.core.entity.result.PageResult;
import com.indigo.core.exception.Ex;
import com.indigo.core.exception.SynapseException;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.IamUserService;
import com.indigo.iam.sdk.dto.users.UserConditionDTO;
import com.indigo.iam.sdk.dto.users.UsersDTO;
import com.indigo.iam.sdk.dto.users.UsersPageDTO;
import com.indigo.iam.sdk.vo.UsersCVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/07/23 07:58
 **/
public interface UserService {

    Users loadUserByUsername(String key);

    Boolean addUser(UsersDTO param);

    Boolean validatePassword(String username, String password);

    Boolean changePassword(String username, String oldPassword, String newPassword);

    List<UsersCVO> listUsers(UserConditionDTO params);

    PageResult<UsersCVO> pageUsers(UsersPageDTO params);
}

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final IamUserService iamUserService;

    @Override
    public Users loadUserByUsername(String key) {
//        Users users = iamUserService.getOneWithDTO();
//
//        if (users == null) {
////            Ex.throwEx();
//        }
        return null;
    }

    @Override
    @GlobalTransactional(rollbackFor = {Exception.class, SynapseException.class})
    public Boolean addUser(UsersDTO param) {
        // 检查用户名是否已存在
        Users existingUser = iamUserService.getOne(new LambdaQueryWrapper<Users>()
                .eq(Users::getAccount, param.getUsername()));
        if (existingUser != null) {
            Ex.throwEx(StandardErrorCode.USER_ALREADY_EXISTS);
        }

        // 检查密码强度
//        PasswordEncoderUtils.PasswordStrength strength = passwordEncoderUtils.checkPasswordStrength(param.getPassword());
//        if (strength == PasswordEncoderUtils.PasswordStrength.WEAK) {
//            log.warn("密码强度太弱: {}", param.getUsername());
//            return false;
//        }

        // 创建用户
        Users users = new Users();
        users.setAccount(param.getUsername());

        // 加密密码

        String encodedPassword = BCrypt.hashpw(param.getPassword(), BCrypt.gensalt());
        users.setPassword(encodedPassword);
        // 设置其他字段
        users.setAccount(param.getUsername());
        // 保存用户
        boolean result = iamUserService.save(users);
        if (result) {
            log.info("用户创建成功: {}", param.getUsername());
        } else {
            log.error("用户创建失败: {}", param.getUsername());
        }

        return result;
    }

    @Override
    public Boolean validatePassword(String username, String password) {
        try {
            Users user = loadUserByUsername(username);
            if (user == null) {
                log.warn("用户不存在: {}", username);
                return false;
            }

            // 验证密码
            boolean isValid = BCrypt.checkpw(password, user.getPassword());
            if (isValid) {
                log.info("密码验证成功: {}", username);
            } else {
                log.warn("密码验证失败: {}", username);
            }

            return isValid;
        } catch (Exception e) {
            log.error("密码验证异常: {}", username, e);
            return false;
        }
    }

    @Override
    public Boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            // 验证旧密码
            if (!validatePassword(username, oldPassword)) {
                log.warn("旧密码验证失败: {}", username);
                return false;
            }

            // 检查新密码强度
//            PasswordEncoderUtils.PasswordStrength strength = passwordEncoderUtils.checkPasswordStrength(newPassword);
//            if (strength == PasswordEncoderUtils.PasswordStrength.WEAK) {
//                log.warn("新密码强度太弱: {}", username);
//                return false;
//            }

            // 获取用户
            Users user = loadUserByUsername(username);
            if (user == null) {
                log.warn("用户不存在: {}", username);
                return false;
            }

            // 加密新密码
            String encodedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setPassword(encodedNewPassword);

            // 更新用户
            boolean result = iamUserService.updateById(user);
            if (result) {
                log.info("密码修改成功: {}", username);
            } else {
                log.error("密码修改失败: {}", username);
            }

            return result;
        } catch (Exception e) {
            log.error("密码修改异常: {}", username, e);
            return false;
        }
    }

    @Override
    public List<UsersCVO> listUsers(UserConditionDTO params) {
        return this.iamUserService.listWithDTO(params, UsersCVO.class);
    }

    @Override
    public PageResult<UsersCVO> pageUsers(UsersPageDTO params) {
        return this.iamUserService.pageWithCondition(params, UsersCVO.class);
    }
}
