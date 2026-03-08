package com.rescue.petrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("lost_found")
public class LostFound {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer type;
    private String petName;
    private String species;
    private String color;
    private String description;
    private String photo;
    private String contactPhone;
    private String location;
    private LocalDate lostDate;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
