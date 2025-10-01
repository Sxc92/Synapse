package com.indigo.mdm.controller;

import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.mdm.repository.service.ICountryService;
import com.indigo.mdm.sdk.dto.CountryDTO;
import com.indigo.mdm.sdk.dto.query.CountryPageDTO;
import com.indigo.mdm.sdk.dto.query.CountryQueryDTO;
import com.indigo.mdm.sdk.vo.CountryVO;
import com.indigo.mdm.service.CountryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/09/26 11:02
 **/
@RestController
@RequestMapping("/country")
@RequiredArgsConstructor
@Validated
public class CountryController {

    private final CountryService countryService;

    private final ICountryService iCountryService;

    @PostMapping("/addOrModify")
    public Result<Boolean> addOrModify(@Valid @RequestBody CountryDTO param) {
        return Result.success(countryService.addOrModify(param));
    }

    @DeleteMapping("/remove")
    public Result<Boolean> remove(@Valid @NotEmpty @RequestBody List<String> ids) {
        return Result.success(iCountryService.removeBatchByIds(ids));
    }

    @PostMapping("/list")
    public Result<List<CountryVO>> list(@RequestBody CountryQueryDTO params) {
        return Result.success(iCountryService.listWithDTO(params, CountryVO.class));
    }

    @PostMapping("/detail")
    public Result<CountryVO> detail(@RequestBody CountryQueryDTO params) {
        return Result.success(iCountryService.getOneWithDTO(params, CountryVO.class));
    }

    @PostMapping("/page")
    public Result<PageResult<CountryVO>> page(@RequestBody CountryPageDTO params) {
        return Result.success(iCountryService.pageWithCondition(params, CountryVO.class));
    }
}
