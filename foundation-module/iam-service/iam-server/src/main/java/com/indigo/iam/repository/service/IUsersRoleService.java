package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.UsersRole;
import com.indigo.iam.repository.mapper.UsersRoleMapper;

/**
 * @author 史偕成
 * @date 2025/11/06 16:52
 **/
@AutoRepository
@IdeFriendlyRepository("iUsersRoleService")
public interface IUsersRoleService extends BaseRepository<UsersRole, UsersRoleMapper> {
}
