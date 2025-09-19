package com.indigo.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.exception.IAMException;
import com.indigo.core.exception.enums.ErrorCode;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.service.IamUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 史偕成
 * @date 2025/07/23 07:58
 **/
public interface UserService {

    Users loadUserByUsername(String key);

}

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final IamUserService iamUserService;

    @Override
    public Users loadUserByUsername(String key) {
        Users users = iamUserService.getOne(new LambdaQueryWrapper<Users>()
                .eq(Users::getAccount, key));
        if (users == null) {
            throw new IAMException(ErrorCode.USER_NOT_FOUND);
        }
        return users;
    }
}
