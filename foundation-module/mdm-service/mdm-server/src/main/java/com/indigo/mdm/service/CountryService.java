package com.indigo.mdm.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.core.exception.Ex;
import com.indigo.mdm.repository.entity.Country;
import com.indigo.mdm.repository.service.ICountryService;
import com.indigo.mdm.sdk.dto.CountryDTO;
import com.indigo.mdm.sdk.dto.enums.MdmError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
        if (countryService.checkKeyUniqueness(param, new String[]{"code"})) {
            Ex.throwEx(MdmError.COUNTRY_CODE_DUPLICATION);
        }
        if (StrUtil.isBlank(param.getId())) {
            Country country = Country.builder().build();
            BeanUtils.copyProperties(param, country);
            return countryService.save(country);
        } else {

        }

        return null;
    }
}
