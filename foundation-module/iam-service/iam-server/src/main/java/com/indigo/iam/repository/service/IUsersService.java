package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.Users;
import com.indigo.iam.repository.mapper.UsersMapper;

/**
 * IAM用户服务接口
 * 
 * 使用说明：
 * 1. 自定义SQL方法统一在 Mapper 接口中定义（如 UsersMapper）
 * 2. 在Service层通过 getMapper() 方法调用 Mapper 的自定义方法
 * 3. 示例：iUsersService.getMapper().getUserByIdCustom(id)
 *
 * @author 史偕成
 * @date 2025/07/22 16:25
 **/
@AutoRepository
@IdeFriendlyRepository("iUsersService")
public interface IUsersService extends BaseRepository<Users, UsersMapper> {
    // 不再使用 default 方法，统一在 Mapper 中定义自定义SQL方法
    // 通过 getMapper() 方法调用 Mapper 的方法
}
