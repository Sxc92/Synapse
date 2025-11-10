package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.service.ISystemService;
import com.indigo.iam.sdk.dto.resource.AddOrModifySystemDTO;
import com.indigo.iam.sdk.dto.resource.SystemDTO;
import com.indigo.iam.sdk.vo.resource.SystemVO;
import com.indigo.iam.service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/08 16:03
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class SystemController {


    private final ISystemService iSystemService;

    private final SystemService systemService;

    /**
     * 添加或修改系统
     *
     * @param param 参数
     * @return 结果
     */
    @PostMapping("/addOrModify")
    public Result<Boolean> addOrModifySystem(@RequestBody AddOrModifySystemDTO param) {
        return Result.success(systemService.addOrModifySystem(param));
    }

    /**
     * 删除系统
     *
     * @param id 系统ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> remove(@PathVariable String id) {
        return Result.success(systemService.deleteSystem(id));
    }

    /**
     * 获取系统列表
     *
     * @return 系统列表
     */
    @PostMapping("/list")
    public Result<List<SystemVO>> list(@RequestBody SystemDTO param) {
        return Result.success(iSystemService.listWithDTO(param, SystemVO.class));
    }

    /**
     * 获取系统分页
     *
     * @return 系统列表
     */
    @PostMapping("/page")
    public Result<PageResult<SystemVO>> page(@RequestBody SystemDTO param) {
        return Result.success(iSystemService.pageWithCondition(param, SystemVO.class));
    }


    /**
     * 获取系统明细
     *
     * @return 系统列表
     */
    @PostMapping("/detail")
    public Result<SystemVO> detail(@RequestBody SystemDTO param) {
        return Result.success(iSystemService.getOneWithDTO(param, SystemVO.class));
    }
}
