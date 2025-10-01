package com.indigo.mdm.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.mdm.repository.entity.Language;
import com.indigo.mdm.repository.mapper.LanguageMapper;

/**
 * @author 史偕成
 * @date 2025/09/30 07:52
 **/
@AutoRepository
@IdeFriendlyRepository("ILangugaeService")
public interface ILangugaeService extends BaseRepository<Language, LanguageMapper> {
}
