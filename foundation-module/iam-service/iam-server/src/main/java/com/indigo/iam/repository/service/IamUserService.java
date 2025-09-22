package com.indigo.iam.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.mapper.IamUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * IAM用户服务接口
 * 
 * @author 史偕成
 * @date 2025/07/22 16:25
 **/
@AutoRepository
@IdeFriendlyRepository("iamUserService")
public interface IamUserService extends BaseRepository<Users,IamUserMapper> {

}


