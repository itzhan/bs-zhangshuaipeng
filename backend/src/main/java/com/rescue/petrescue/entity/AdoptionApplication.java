package com.rescue.petrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("adoption_application")
public class AdoptionApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long petId;
    private Long userId;
    private String realName;
    private String phone;
    private String address;
    private String reason;
    private String experience;
    private String livingCondition;
    private Integer status;
    private String reviewNote;
    private Long reviewerId;
    private LocalDateTime reviewTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
