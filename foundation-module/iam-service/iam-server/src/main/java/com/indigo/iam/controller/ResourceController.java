package com.indigo.iam.controller;

import com.indigo.core.context.UserContext;
import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.service.IResourceService;
import com.indigo.iam.sdk.dto.opera.AddOrModifyResourceDTO;
import com.indigo.iam.sdk.dto.query.ResourceDTO;
import com.indigo.iam.sdk.vo.resource.ResourceVO;
import com.indigo.iam.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 史偕成
 * @date 2025/11/10
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final IResourceService iResourceService;

    private final ResourceService iamResourceService;


    /**
     * 添加或修改资源
     *
     * @param param 参数
     * @return 结果
     */
    @PostMapping("/addOrModify")
    public Result<Boolean> addOrModifyResource(@RequestBody AddOrModifyResourceDTO param) {
        return Result.success(iamResourceService.addOrModifyResource(param));
    }

    /**
     * 删除资源
     *
     * @param id 资源ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> remove(@PathVariable String id) {
        return Result.success(iamResourceService.deleteResource(id));
    }

    /**
     * 获取资源列表
     *
     * @param param 查询参数
     * @return 资源列表
     */
    @PostMapping("/list")
    public Result<List<ResourceVO>> list(@RequestBody ResourceDTO param) {
        return Result.success(iResourceService.listWithDTO(param, ResourceVO.class));
    }

    /**
     * 获取资源分页
     *
     * @param param 查询参数
     * @return 资源分页结果
     */
    @PostMapping("/page")
    public Result<PageResult<ResourceVO>> page(@RequestBody ResourceDTO param) {
        return Result.success(iResourceService.pageWithCondition(param, ResourceVO.class));
    }

    /**
     * 获取资源明细
     *
     * @param param 查询参数
     * @return 资源明细
     */
    @PostMapping("/detail")
    public Result<ResourceVO> detail(@RequestBody ResourceDTO param) {
        return Result.success(iResourceService.getOneWithDTO(param, ResourceVO.class));
    }

    @GetMapping("/code")
    public Result<List<String>> getCurrentResourceCode() {
        return Result.success(UserContext.getCurrentPermissions());
    }
}

