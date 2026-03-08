package com.rescue.petrescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pet")
public class Pet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String age;
    private Integer gender;
    private BigDecimal weight;
    private String color;
    private String healthStatus;
    private Integer isSterilized;
    private Integer isVaccinated;
    private String description;
    private String photo;
    private String photos;
    private String location;
    private LocalDate rescueDate;
    private Long stationId;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
