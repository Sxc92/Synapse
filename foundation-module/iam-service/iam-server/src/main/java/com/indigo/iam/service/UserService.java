package com.indigo.iam.service;

import com.indigo.iam.repository.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 史偕成
 * @date 2025/07/23 07:58
 **/
public interface UserService {

    Users loadUserByUsername(String username);

}

@Slf4j
@Service
class UserServiceImpl implements UserService {


    @Override
    public Users loadUserByUsername(String username) {
        return null;
    }
}
