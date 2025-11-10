package com.indigo.iam.repository.mapper;

import com.indigo.databases.mapper.EnhancedVoMapper;
import com.indigo.iam.repository.entity.Menu;
import com.indigo.iam.sdk.vo.resource.MenuVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 史偕成
 * @date 2025/11/08 17:12
 **/
@Mapper
public interface MenuMapper extends EnhancedVoMapper<Menu, MenuVO> {
}
