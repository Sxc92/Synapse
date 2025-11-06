package com.indigo.iam.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.iam.repository.entity.Roles;

/**
 * @author 史偕成
 * @date 2025/11/06 16:37
 **/
@AutoRepository
@IdeFriendlyRepository("IRoleService")
public interface IRoleService extends IService<Roles> {
}
