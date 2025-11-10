package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.repository.mapper.MenuMapper;

/**
 * @author 史偕成
 * @date 2025/11/08 17:15
 **/
@AutoRepository
@IdeFriendlyRepository("iMenuService")
public interface IMenuService extends BaseRepository<Menu, MenuMapper> {
}
