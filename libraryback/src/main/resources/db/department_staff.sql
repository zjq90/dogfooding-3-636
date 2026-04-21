-- 部门管理和内部人员管理功能数据库脚本

USE library_db;

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(30) NOT NULL UNIQUE COMMENT '部门编号(格式：B-数字)',
    parent_id BIGINT DEFAULT 0 COMMENT '上级部门ID，0为顶级部门',
    description VARCHAR(255) COMMENT '部门描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_dept_code (dept_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    FOREIGN KEY (parent_id) REFERENCES sys_department(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 内部人员表
CREATE TABLE IF NOT EXISTS sys_staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人员ID',
    staff_name VARCHAR(50) NOT NULL COMMENT '姓名',
    staff_code VARCHAR(30) NOT NULL UNIQUE COMMENT '人员编号',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名(用于登录)',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    department_id BIGINT NOT NULL COMMENT '所属部门ID',
    position VARCHAR(50) COMMENT '职位',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    entry_date DATE COMMENT '入职日期',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_staff_code (staff_code),
    INDEX idx_department_id (department_id),
    INDEX idx_username (username),
    INDEX idx_status (status),
    FOREIGN KEY (department_id) REFERENCES sys_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内部人员表';

-- 权限表（菜单权限）
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    permission_type TINYINT NOT NULL COMMENT '权限类型：1-菜单，2-按钮',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID，0为顶级',
    path VARCHAR(100) COMMENT '路由路径(菜单类型)',
    component VARCHAR(100) COMMENT '组件路径(菜单类型)',
    icon VARCHAR(50) COMMENT '图标(菜单类型)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_type (permission_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 部门权限关联表
CREATE TABLE IF NOT EXISTS sys_dept_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    department_id BIGINT NOT NULL COMMENT '部门ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_dept_permission (department_id, permission_id),
    FOREIGN KEY (department_id) REFERENCES sys_department(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门权限关联表';

-- 插入部门数据
INSERT INTO sys_department (dept_name, dept_code, parent_id, description, sort_order, status) VALUES
('董事会', 'B-001', 0, '公司最高决策层', 1, 1),
('总经理办公室', 'B-002', 0, '公司日常管理', 2, 1),
('技术部', 'B-003', 0, '技术研发与支持', 3, 1),
('前端开发组', 'B-003-001', 3, '前端应用开发', 1, 1),
('后端开发组', 'B-003-002', 3, '后端服务开发', 2, 1),
('测试组', 'B-003-003', 3, '软件测试', 3, 1),
('产品部', 'B-004', 0, '产品规划与设计', 4, 1),
('市场部', 'B-005', 0, '市场推广与销售', 5, 1),
('人事部', 'B-006', 0, '人力资源与行政', 6, 1),
('财务部', 'B-007', 0, '财务管理', 7, 1);

-- 插入权限数据（菜单权限）
INSERT INTO sys_permission (permission_name, permission_code, permission_type, parent_id, path, component, icon, sort_order, status) VALUES
-- 一级菜单
('数据概览', 'dashboard', 1, 0, '/dashboard', 'dashboard/index', 'el-icon-s-data', 1, 1),
('图书管理', 'book', 1, 0, '/books', 'book/index', 'el-icon-reading', 2, 1),
('分类管理', 'category', 1, 0, '/categories', 'category/index', 'el-icon-folder-opened', 3, 1),
('借阅管理', 'borrow', 1, 0, '/borrow', 'borrow/index', 'el-icon-document', 4, 1),
('用户管理', 'user', 1, 0, '/users', 'user/index', 'el-icon-user', 5, 1),
('部门管理', 'department', 1, 0, '/departments', 'department/index', 'el-icon-office-building', 6, 1),
('内部人员', 'staff', 1, 0, '/staff', 'staff/index', 'el-icon-s-custom', 7, 1),

-- 图书管理按钮权限
('图书新增', 'book:add', 2, 2, NULL, NULL, NULL, 1, 1),
('图书编辑', 'book:edit', 2, 2, NULL, NULL, NULL, 2, 1),
('图书删除', 'book:delete', 2, 2, NULL, NULL, NULL, 3, 1),
('图书导出', 'book:export', 2, 2, NULL, NULL, NULL, 4, 1),

-- 分类管理按钮权限
('分类新增', 'category:add', 2, 3, NULL, NULL, NULL, 1, 1),
('分类编辑', 'category:edit', 2, 3, NULL, NULL, NULL, 2, 1),
('分类删除', 'category:delete', 2, 3, NULL, NULL, NULL, 3, 1),

-- 借阅管理按钮权限
('借阅新增', 'borrow:add', 2, 4, NULL, NULL, NULL, 1, 1),
('借阅编辑', 'borrow:edit', 2, 4, NULL, NULL, NULL, 2, 1),
('借阅删除', 'borrow:delete', 2, 4, NULL, NULL, NULL, 3, 1),
('归还图书', 'borrow:return', 2, 4, NULL, NULL, NULL, 4, 1),

-- 用户管理按钮权限
('用户新增', 'user:add', 2, 5, NULL, NULL, NULL, 1, 1),
('用户编辑', 'user:edit', 2, 5, NULL, NULL, NULL, 2, 1),
('用户删除', 'user:delete', 2, 5, NULL, NULL, NULL, 3, 1),
('用户状态', 'user:status', 2, 5, NULL, NULL, NULL, 4, 1),

-- 部门管理按钮权限
('部门新增', 'department:add', 2, 6, NULL, NULL, NULL, 1, 1),
('部门编辑', 'department:edit', 2, 6, NULL, NULL, NULL, 2, 1),
('部门删除', 'department:delete', 2, 6, NULL, NULL, NULL, 3, 1),
('部门状态', 'department:status', 2, 6, NULL, NULL, NULL, 4, 1),

-- 内部人员按钮权限
('人员新增', 'staff:add', 2, 7, NULL, NULL, NULL, 1, 1),
('人员编辑', 'staff:edit', 2, 7, NULL, NULL, NULL, 2, 1),
('人员删除', 'staff:delete', 2, 7, NULL, NULL, NULL, 3, 1),
('人员状态', 'staff:status', 2, 7, NULL, NULL, NULL, 4, 1),
('权限设置', 'staff:permission', 2, 7, NULL, NULL, NULL, 5, 1);

-- 插入内部人员数据（密码：123456，加密后）
-- 使用 BCrypt 加密后的密码：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO
INSERT INTO sys_staff (staff_name, staff_code, username, password, phone, email, department_id, position, gender, entry_date, status) VALUES
('张三', 'S-001', 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138001', 'zhangsan@library.com', 3, '技术总监', 1, '2020-01-15', 1),
('李四', 'S-002', 'lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138002', 'lisi@library.com', 4, '前端组长', 1, '2020-03-20', 1),
('王五', 'S-003', 'wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138003', 'wangwu@library.com', 5, '后端组长', 1, '2020-03-20', 1),
('赵六', 'S-004', 'zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138004', 'zhaoliu@library.com', 6, '测试组长', 1, '2020-06-10', 1),
('钱七', 'S-005', 'qianqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138005', 'qianqi@library.com', 7, '产品经理', 2, '2020-05-15', 1),
('孙八', 'S-006', 'sunba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138006', 'sunba@library.com', 8, '市场经理', 1, '2020-07-01', 1),
('周九', 'S-007', 'zhoujiu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138007', 'zhoujiu@library.com', 9, '人事经理', 2, '2020-04-20', 1),
('吴十', 'S-008', 'wushi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138008', 'wushi@library.com', 10, '财务经理', 1, '2020-02-10', 1),
('郑十一', 'S-009', 'zhengshiyi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138009', 'zhengshiyi@library.com', 4, '前端开发', 1, '2021-03-15', 1),
('王十二', 'S-010', 'wangshier', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '13800138010', 'wangshier@library.com', 5, '后端开发', 1, '2021-06-20', 1);

-- 给技术部及其下属部门分配权限（拥有所有权限）
INSERT INTO sys_dept_permission (department_id, permission_id)
SELECT 3, id FROM sys_permission WHERE status = 1;

-- 给产品部分配图书管理、分类管理、借阅管理权限
INSERT INTO sys_dept_permission (department_id, permission_id) VALUES
(7, 1), (7, 2), (7, 8), (7, 9), (7, 10), (7, 11), (7, 3), (7, 12), (7, 13), (7, 14), (7, 4), (7, 15), (7, 16), (7, 17), (7, 18);

-- 给市场部分配借阅管理、用户管理权限
INSERT INTO sys_dept_permission (department_id, permission_id) VALUES
(8, 1), (8, 4), (8, 15), (8, 16), (8, 17), (8, 18), (8, 5), (8, 19), (8, 20), (8, 21), (8, 22);

-- 给人事部分配用户管理、部门管理、内部人员管理权限
INSERT INTO sys_dept_permission (department_id, permission_id) VALUES
(9, 1), (9, 5), (9, 19), (9, 20), (9, 21), (9, 22), (9, 6), (9, 23), (9, 24), (9, 25), (9, 26), (9, 7), (9, 27), (9, 28), (9, 29), (9, 30), (9, 31);

-- 给财务部分配数据概览、借阅管理权限（只读）
INSERT INTO sys_dept_permission (department_id, permission_id) VALUES
(10, 1), (10, 4), (10, 15), (10, 16), (10, 17), (10, 18);
