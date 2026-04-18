package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("SELECT * FROM sys_menu WHERE deleted = 0 ORDER BY sort_order, id")
    List<Menu> selectAll();

    @Select("SELECT m.* FROM sys_menu m " +
            "INNER JOIN sys_employee_permission ep ON m.id = ep.menu_id " +
            "WHERE ep.employee_id = #{employeeId} AND m.deleted = 0 " +
            "ORDER BY m.sort_order, m.id")
    List<Menu> selectByEmployeeId(@Param("employeeId") Long employeeId);
}
