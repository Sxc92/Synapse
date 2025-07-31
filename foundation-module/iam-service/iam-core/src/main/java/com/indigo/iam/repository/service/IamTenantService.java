//package com.indigo.iam.repository.service;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.indigo.iam.api.model.dto.TenantsPageDTO;
//import com.indigo.iam.api.model.pojo.IamTenant;
//import com.indigo.iam.repository.mapper.IamTenantMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author 史偕成
// * @date 2025/07/28 06:12
// **/
//public interface IamTenantService extends IService<IamTenant> {
//
//    Boolean addOrModify(IamTenant iamTenant);
//
//    IPage<IamTenant> getPage(IPage<IamTenant> page, TenantsPageDTO params);
//}
//
//@Service
//@Slf4j
//class IamTenantServiceImpl extends ServiceImpl<IamTenantMapper, IamTenant> implements IamTenantService {
//
//    private static final Map<String, SFunction<IamTenant, ?>> columnMap = new HashMap<>();
//
//    static {
//        columnMap.put("code", IamTenant::getCode);
//        columnMap.put("description", IamTenant::getDescription);
//        columnMap.put("status", IamTenant::getStatus);
//        columnMap.put("expireTime", IamTenant::getExpireTime);
//        columnMap.put("id", IamTenant::getId);
//        columnMap.put("createTime", IamTenant::getCreateTime);
//        columnMap.put("modifyTime", IamTenant::getModifyTime);
//    }
//
//    @Override
//    public Boolean addOrModify(IamTenant iamTenant) {
//        IamTenant iamTenant1 = this.baseMapper.selectById(iamTenant.getId());
//        return saveOrUpdate(iamTenant);
//    }
//
//    @Override
//    public IPage<IamTenant> getPage(IPage<IamTenant> page, TenantsPageDTO params) {
//        LambdaQueryWrapper<IamTenant> wrapper = new LambdaQueryWrapper<IamTenant>()
//                .eq(StrUtil.isNotBlank(params.getCode()), IamTenant::getCode, params.getCode())
//                .in(CollUtil.isNotEmpty(params.getStatus()), IamTenant::getStatus, params.getStatus())
//                .like(StrUtil.isNotBlank(params.getDescription()), IamTenant::getDescription, params.getDescription());
//        if (StrUtil.isNotBlank(params.getSortField())) {
//            SFunction<IamTenant, ?> column = columnMap.get(params.getSortField());
//            if (column != null) {
//                if ("asc".equalsIgnoreCase(params.getSorted())) {
//                    wrapper.orderByAsc(column);
//                } else {
//                    wrapper.orderByDesc(column);
//                }
//            }
//        }
//        return this.baseMapper.selectPage(page, wrapper);
//    }
//}