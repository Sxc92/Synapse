package com.indigo.mdm.controller;

import com.indigo.core.entity.Result;
import com.indigo.mdm.sdk.dto.CountryDTO;
import com.indigo.mdm.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 史偕成
 * @date 2025/09/26 11:02
 **/
@RestController
@RequestMapping("/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;


    @PostMapping("/addOrModify")
    public Result<Boolean> addOrModify(@RequestBody CountryDTO param) {
        return Result.success(countryService.addOrModify(param));
    }
}
