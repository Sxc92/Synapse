package com.indigo.mdm.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.mdm.repository.entity.Language;
import com.indigo.mdm.repository.mapper.LanguageMapper;
import org.springframework.stereotype.Service;

/**
 * @author 史偕成
 * @title
 * @description
 * @create 2025-09-23 13:52
 */
@Service
@AutoRepository(value = "ILanguageService", enableCache = true, cachePrefix = "language_")
public interface ILanguageService extends BaseRepository<Language, LanguageMapper> {
}
