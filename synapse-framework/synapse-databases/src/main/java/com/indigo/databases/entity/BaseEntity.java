package com.indigo.databases.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 通用基础实体类，适用于大部分业务表（不建议用于纯中间关联表）
 * 包含主键、乐观锁、逻辑删除标记
 *
 * @author 史偕成
 * @date 2025/07/22 15:58
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseEntity<T> implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private T id;

}
