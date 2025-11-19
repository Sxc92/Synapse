package com.indigo.iam.controller;

import com.indigo.core.entity.Result;
import com.indigo.core.entity.result.PageResult;
import com.indigo.iam.repository.service.IMenuService;
import com.indigo.iam.sdk.dto.opera.AddOrModifyMenuDTO;
import com.indigo.iam.sdk.dto.query.MenuDTO;
import com.indigo.iam.sdk.vo.resource.MenuTreeVO;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import com.indigo.iam.service.MenuService;
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
@RequestMapping("/menu")
public class MenuController {

    private final IMenuService iMenuService;

    private final MenuService menuService;

    /**
     * 添加或修改菜单
     *
     * @param param 参数
     * @return 结果
     */
    @PostMapping("/addOrModify")
    public Result<Boolean> addOrModifyMenu(@RequestBody AddOrModifyMenuDTO param) {
        return Result.success(menuService.addOrModifyMenu(param));
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> remove(@PathVariable String id) {
        return Result.success(menuService.deleteMenu(id));
    }

    /**
     * 获取菜单列表
     *
     * @param param 查询参数
     * @return 菜单列表
     */
    @PostMapping("/list")
    public Result<List<MenuVO>> list(@RequestBody MenuDTO param) {
        return Result.success(iMenuService.listWithDTO(param, MenuVO.class));
    }

    /**
     * 获取菜单分页
     *
     * @param param 查询参数
     * @return 菜单分页结果
     */
    @PostMapping("/page")
    public Result<PageResult<MenuVO>> page(@RequestBody MenuDTO param) {
        return Result.success(iMenuService.pageWithCondition(param, MenuVO.class));
    }

    /**
     * 获取菜单明细
     *
     * @param param 查询参数
     * @return 菜单明细
     */
    @PostMapping("/detail")
    public Result<MenuVO> detail(@RequestBody MenuDTO param) {
        return Result.success(iMenuService.getOneWithDTO(param, MenuVO.class));
    }

    /**
     * 获取菜单树结构列表
     *
     * @param param 查询参数
     * @return 菜单树结构列表
     */
    @PostMapping("/tree/list")
    public Result<List<MenuTreeVO>> listTree(@RequestBody MenuDTO param) {
        return Result.success(menuService.listTree(param));
    }

    /**
     * 获取菜单树结构分页
     *
     * @param param 分页查询参数
     * @return 菜单树结构分页结果
     */
    @PostMapping("/tree/page")
    public Result<PageResult<MenuTreeVO>> pageTree(@RequestBody MenuDTO param) {
        return Result.success(menuService.pageTree(param));
    }

    /**
     * 获取菜单树结构详情（包含子节点）
     *
     * @param param 查询参数
     * @return 菜单树结构详情
     */
    @PostMapping("/tree/detail")
    public Result<MenuTreeVO> detailTree(@RequestBody MenuDTO param) {
        return Result.success(menuService.detailTree(param));
    }
}

