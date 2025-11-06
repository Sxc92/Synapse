package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.mapper.UsersMapper;

/**
 * IAM用户服务接口
 *
 * @author 史偕成
 * @date 2025/07/22 16:25
 **/
@AutoRepository
@IdeFriendlyRepository("iUsersService")
public interface IUsersService extends BaseRepository<Users, UsersMapper> {

}


