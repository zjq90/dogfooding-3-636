package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Department;
import com.library.mapper.DepartmentMapper;
import com.library.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<Department> getAllDepartments() {
        return baseMapper.selectAll();
    }

    @Override
    public List<Department> getDepartmentTree() {
        List<Department> allDepartments = getAllDepartments();

        List<Department> rootDepartments = allDepartments.stream()
                .filter(d -> d.getParentId() == null || d.getParentId() == 0)
                .collect(Collectors.toList());

        for (Department root : rootDepartments) {
            buildDepartmentTree(root, allDepartments);
        }

        log.debug("构建部门树完成，根节点数量: {}", rootDepartments.size());
        return rootDepartments;
    }

    private void buildDepartmentTree(Department parent, List<Department> allDepartments) {
        List<Department> children = allDepartments.stream()
                .filter(d -> parent.getId() != null && parent.getId().equals(d.getParentId()))
                .collect(Collectors.toList());

        parent.setChildren(children);

        for (Department child : children) {
            buildDepartmentTree(child, allDepartments);
        }
    }
}
