package com.indigo.mdm.service;

import cn.hutool.core.util.StrUtil;
import com.indigo.mdm.repository.entity.Country;
import com.indigo.mdm.repository.service.ICountryService;
import com.indigo.mdm.sdk.dto.CountryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 史偕成
 * @date 2025/09/26 11:15
 **/
public interface CountryService {
    Boolean addOrModify(CountryDTO param);
}

@Service
@Slf4j
@RequiredArgsConstructor
class CountryServiceImpl implements CountryService {

    private final ICountryService countryService;

    @Override
    public Boolean addOrModify(CountryDTO param) {

        return null;
    }
}
