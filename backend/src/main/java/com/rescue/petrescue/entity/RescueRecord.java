package com.rescue.petrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rescue_record")
public class RescueRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long petId;
    private Long reporterId;
    private Long rescuerId;
    private String location;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String description;
    private String photo;
    private Integer status;
    private LocalDateTime rescueDate;
    private LocalDateTime finishDate;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
