package com.indigo.mdm.repository.service;

import com.indigo.databases.annotation.AutoRepository;
import com.indigo.databases.annotation.IdeFriendlyRepository;
import com.indigo.databases.repository.BaseRepository;
import com.indigo.mdm.repository.entity.Country;
import com.indigo.mdm.repository.mapper.CountryMapper;

/**
 * @author 史偕成
 * @date 2025/09/26 11:01
 **/
@AutoRepository
@IdeFriendlyRepository("ICountryService")
public interface ICountryService extends BaseRepository<Country, CountryMapper> {
}
