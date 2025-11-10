package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.RoleResource;
import com.indigo.iam.repository.mapper.RoleResourceMapper;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
@AutoRepository
@IdeFriendlyRepository("iRoleResourceService")
public interface IRoleResourceService extends BaseRepository<RoleResource, RoleResourceMapper> {
}

