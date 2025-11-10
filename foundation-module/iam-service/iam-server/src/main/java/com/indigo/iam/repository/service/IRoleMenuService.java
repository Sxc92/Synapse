package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.RoleMenu;
import com.indigo.iam.repository.mapper.RoleMenuMapper;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
@AutoRepository
@IdeFriendlyRepository("iRoleMenuService")
public interface IRoleMenuService extends BaseRepository<RoleMenu, RoleMenuMapper> {
}

