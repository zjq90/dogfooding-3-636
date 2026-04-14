package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_employee_permission")
public class EmployeePermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    private Long menuId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
