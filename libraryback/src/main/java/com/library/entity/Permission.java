package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String permissionName;

    private String permissionCode;

    private Integer permissionType;

    private Long parentId;

    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(select = false)
    private Integer deleted;

    @TableField(exist = false)
    private String parentName;

    @TableField(exist = false)
    private List<Permission> children;

    @TableField(exist = false)
    private Boolean hasPermission;
}
