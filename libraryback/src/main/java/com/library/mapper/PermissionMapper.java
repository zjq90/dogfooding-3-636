package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> selectByDepartmentId(@Param("departmentId") Long departmentId);

    List<Permission> selectMenuPermissions();

    List<Permission> selectButtonPermissions();

    List<Permission> selectAllMenus();
}
