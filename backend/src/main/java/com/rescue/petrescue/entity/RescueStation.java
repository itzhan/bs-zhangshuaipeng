package com.rescue.petrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rescue_station")
public class RescueStation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer capacity;
    private Integer currentCount;
    private String contactPhone;
    private String manager;
    private String description;
    private String photo;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
