package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_employee")
public class Employee {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String employeeNo;

    private Long departmentId;

    private String phone;

    private String email;

    private String position;

    private Integer gender;

    private LocalDate birthday;

    private String avatar;

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
    private List<Long> permissionIds;
}
