package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_staff")
public class Staff {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String staffName;

    private String staffCode;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phone;

    private String email;

    private String avatar;

    private Long departmentId;

    private String position;

    private Integer gender;

    private LocalDate entryDate;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(select = false)
    private Integer deleted;

    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    private String deptCode;

    @TableField(exist = false)
    private List<String> permissions;
}
