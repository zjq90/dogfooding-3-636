package com.library.controller;

import com.library.common.Result;
import com.library.entity.Menu;
import com.library.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public Result<List<Menu>> getAllMenus(@RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看菜单列表");
        }
        List<Menu> menus = menuService.getAllMenus();
        return Result.success(menus);
    }

    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree(@RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看菜单树");
        }
        List<Menu> menus = menuService.getMenuTree();
        return Result.success(menus);
    }

    @GetMapping("/employee/{employeeId}")
    public Result<List<Menu>> getMenusByEmployeeId(@PathVariable Long employeeId, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看人员权限");
        }
        List<Menu> menus = menuService.getMenusByEmployeeId(employeeId);
        return Result.success(menus);
    }
}
