package com.indigo.iam.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.indigo.iam.api.model.pojo.IamUser;
import com.indigo.iam.repository.mapper.IamUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 史偕成
 * @date 2025/07/22 16:25
 **/
public interface IamUserService extends IService<IamUser> {

}

@Service
@Slf4j
class IamUserServiceImpl extends ServiceImpl<IamUserMapper, IamUser> implements IamUserService {
}
