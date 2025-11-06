package com.indigo.iam.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.iam.repository.entity.UsersRole;

/**
 * @author 史偕成
 * @date 2025/11/06 16:52
 **/
@AutoRepository
@IdeFriendlyRepository("iUsersRoleService")
public interface IUsersRoleService extends IService<UsersRole> {
}
