package com.indigo.iam.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.iam.repository.entity.IamSystem;
import com.indigo.iam.repository.mapper.SystemMapper;

/**
 * @author 史偕成
 * @date 2025/11/08 15:43
 **/
@AutoRepository
@IdeFriendlyRepository("iSystemService")
public interface ISystemService extends BaseRepository<IamSystem, SystemMapper> {
}
