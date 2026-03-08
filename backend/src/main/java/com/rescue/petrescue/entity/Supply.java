package com.rescue.petrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("supply")
public class Supply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private Integer quantity;
    private String unit;
    private Long stationId;
    private Integer threshold;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
