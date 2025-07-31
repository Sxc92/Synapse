package com.indigo.databases.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 带完整审计信息（创建+修改）的基础实体类
 *
 * @author 史偕成
 * @date 2025/07/22 16:02
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuditEntity<T> extends CreatedEntity<T> {

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer revision;

    /**
     * 逻辑删除标记（true=已删除，false=未删除）
     */
    @TableLogic(delval = "0", value = "1")
    private Boolean deleted;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT, value = "modify_time")
    private LocalDateTime modifyTime;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT, value = "modify_user")
    private T modifyUser;
}
