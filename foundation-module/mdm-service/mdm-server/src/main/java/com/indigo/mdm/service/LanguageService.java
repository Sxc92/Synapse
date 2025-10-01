package com.indigo.mdm.service;

import cn.hutool.core.util.StrUtil;
import com.indigo.core.exception.Ex;
import com.indigo.mdm.repository.entity.Country;
import com.indigo.mdm.repository.entity.Language;
import com.indigo.mdm.repository.mapper.LanguageMapper;
import com.indigo.mdm.repository.service.ILangugaeService;
import com.indigo.mdm.sdk.dto.LanguageDTO;
import com.indigo.mdm.sdk.enums.MdmError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author 史偕成
 * @date 2025/09/30 08:49
 **/
public interface LanguageService {

    boolean addOrModify(LanguageDTO param);
}

@RequiredArgsConstructor
@Service
@Slf4j
class LanguageServiceImpl implements LanguageService {

    private final ILangugaeService iLangugaeService;

    @Override
    public boolean addOrModify(LanguageDTO param) {
        if (iLangugaeService.checkKeyUniqueness(param, "code")) {
            Ex.throwEx(MdmError.LANGUAGE_CODE_DUPLICATION);
        }
        if (StrUtil.isBlank(param.getId())) {
            Language language = Language.builder().build();
            BeanUtils.copyProperties(param, language);
            return iLangugaeService.save(language);
        } else {
            Language language = this.iLangugaeService.getById(param.getId());
            if (language == null) {
                Ex.throwEx(MdmError.LANGUAGE_NOT_EXIST);
            }
            language.setName(param.getName());
            return iLangugaeService.updateById(language);
        }
    }
}
