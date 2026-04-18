-- 图书借阅管理系统数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 图书分类表
CREATE TABLE IF NOT EXISTS book_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(30) NOT NULL UNIQUE COMMENT '分类编码',
    description VARCHAR(255) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0为顶级分类',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 图书信息表
CREATE TABLE IF NOT EXISTS book_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN编号',
    title VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    category_id BIGINT COMMENT '分类ID',
    description TEXT COMMENT '图书简介',
    cover_image VARCHAR(255) COMMENT '封面图片',
    price DECIMAL(10,2) COMMENT '价格',
    total_quantity INT DEFAULT 0 COMMENT '总库存',
    available_quantity INT DEFAULT 0 COMMENT '可借数量',
    location VARCHAR(50) COMMENT '存放位置',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-可借，2-借完',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_isbn (isbn),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_title (title),
    FOREIGN KEY (category_id) REFERENCES book_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_date DATE NOT NULL COMMENT '借阅日期',
    due_date DATE NOT NULL COMMENT '应还日期',
    return_date DATE COMMENT '实际归还日期',
    status TINYINT DEFAULT 0 COMMENT '状态：0-借阅中，1-已归还，2-逾期',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (book_id) REFERENCES book_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 默认管理员用户由应用启动时自动创建
-- 用户名: admin, 密码: admin123

-- 插入图书分类数据
INSERT INTO book_category (name, code, description, sort_order, parent_id) VALUES
('文学', 'LITERATURE', '文学类图书', 1, 0),
('小说', 'NOVEL', '小说类图书', 2, 0),
('科技', 'TECHNOLOGY', '科技类图书', 3, 0),
('历史', 'HISTORY', '历史类图书', 4, 0),
('艺术', 'ART', '艺术类图书', 5, 0),
('教育', 'EDUCATION', '教育类图书', 6, 0),
('经济', 'ECONOMICS', '经济类图书', 7, 0),
('医学', 'MEDICINE', '医学类图书', 8, 0);

-- 插入示例图书数据
INSERT INTO book_info (isbn, title, author, publisher, publish_date, category_id, description, price, total_quantity, available_quantity, location, status) VALUES
('978-7-111-1', '红楼梦', '曹雪芹', '人民文学出版社', '2020-01-15', 1, '中国古典文学四大名著之一', 45.00, 10, 8, 'A区-01-01', 1),
('978-7-111-2', '西游记', '吴承恩', '人民文学出版社', '2020-03-20', 1, '中国古典文学四大名著之一', 42.00, 8, 6, 'A区-01-02', 1),
('978-7-111-3', '三体', '刘慈欣', '重庆出版社', '2019-06-01', 2, '科幻小说巅峰之作', 58.00, 15, 12, 'B区-02-01', 1),
('978-7-111-4', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '2021-08-10', 3, 'Java程序员必读经典', 108.00, 5, 3, 'C区-03-01', 1),
('978-7-111-5', 'Spring实战', 'Craig Walls', '人民邮电出版社', '2022-02-28', 3, 'Spring框架实战指南', 89.00, 6, 4, 'C区-03-02', 1),
('978-7-111-6', '明朝那些事儿', '当年明月', '中国友谊出版公司', '2018-11-01', 4, '历史通俗读物', 168.00, 7, 5, 'D区-04-01', 1),
('978-7-111-7', '艺术的故事', '贡布里希', '广西美术出版社', '2017-05-15', 5, '艺术史经典著作', 280.00, 3, 2, 'E区-05-01', 1),
('978-7-111-8', '深度学习', 'Ian Goodfellow', '人民邮电出版社', '2021-09-20', 3, '人工智能领域经典教材', 128.00, 4, 2, 'C区-03-03', 1);

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    code VARCHAR(30) NOT NULL UNIQUE COMMENT '部门编号(格式：B-数字)',
    parent_id BIGINT DEFAULT 0 COMMENT '上级部门ID，0为顶级部门',
    leader VARCHAR(50) COMMENT '部门负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '部门地址',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 内部人员表
CREATE TABLE IF NOT EXISTS sys_employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人员ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    employee_no VARCHAR(30) NOT NULL UNIQUE COMMENT '员工编号',
    department_id BIGINT NOT NULL COMMENT '所属部门ID',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    position VARCHAR(50) COMMENT '职位',
    gender TINYINT DEFAULT 1 COMMENT '性别：0-女，1-男',
    birthday DATE COMMENT '出生日期',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：0-离职，1-在职',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_department_id (department_id),
    INDEX idx_employee_no (employee_no),
    INDEX idx_status (status),
    FOREIGN KEY (department_id) REFERENCES sys_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内部人员表';

-- 菜单权限表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜单ID',
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '菜单编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    path VARCHAR(100) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '菜单图标',
    type TINYINT DEFAULT 1 COMMENT '类型：1-菜单，2-按钮',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 人员权限关联表
CREATE TABLE IF NOT EXISTS sys_employee_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    employee_id BIGINT NOT NULL COMMENT '人员ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_employee_menu (employee_id, menu_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_menu_id (menu_id),
    FOREIGN KEY (employee_id) REFERENCES sys_employee(id),
    FOREIGN KEY (menu_id) REFERENCES sys_menu(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员权限关联表';

-- 插入部门测试数据
INSERT INTO sys_department (name, code, parent_id, leader, phone, email, address, sort_order, status) VALUES
('总公司', 'B-001', 0, '张总', '13800138000', 'ceo@company.com', '北京市朝阳区总部大厦', 1, 1),
('技术部', 'B-002', 1, '李经理', '13800138001', 'tech@company.com', '总部大厦5层', 2, 1),
('人事部', 'B-003', 1, '王经理', '13800138002', 'hr@company.com', '总部大厦3层', 3, 1),
('财务部', 'B-004', 1, '赵经理', '13800138003', 'finance@company.com', '总部大厦4层', 4, 1),
('运营部', 'B-005', 1, '刘经理', '13800138004', 'operation@company.com', '总部大厦2层', 5, 1),
('前端组', 'B-006', 2, '陈组长', '13800138005', 'frontend@company.com', '5层A区', 1, 1),
('后端组', 'B-007', 2, '林组长', '13800138006', 'backend@company.com', '5层B区', 2, 1),
('测试组', 'B-008', 2, '周组长', '13800138007', 'test@company.com', '5层C区', 3, 1);

-- 插入内部人员测试数据
INSERT INTO sys_employee (name, employee_no, department_id, phone, email, position, gender, status) VALUES
('张三', 'EMP001', 1, '13900139001', 'zhangsan@company.com', 'CEO', 1, 1),
('李四', 'EMP002', 2, '13900139002', 'lisi@company.com', '技术总监', 1, 1),
('王五', 'EMP003', 3, '13900139003', 'wangwu@company.com', '人事主管', 0, 1),
('赵六', 'EMP004', 4, '13900139004', 'zhaoliu@company.com', '财务主管', 1, 1),
('刘七', 'EMP005', 5, '13900139005', 'liuqi@company.com', '运营主管', 0, 1),
('陈明', 'EMP006', 6, '13900139006', 'chenming@company.com', '前端工程师', 1, 1),
('林华', 'EMP007', 7, '13900139007', 'linhua@company.com', '后端工程师', 1, 1),
('周婷', 'EMP008', 8, '13900139008', 'zhouting@company.com', '测试工程师', 0, 1),
('黄强', 'EMP009', 6, '13900139009', 'huangqiang@company.com', '前端工程师', 1, 1),
('吴芳', 'EMP010', 7, '13900139010', 'wufang@company.com', '后端工程师', 0, 1);

-- 插入菜单权限数据(基于系统实际功能)
INSERT INTO sys_menu (name, code, parent_id, path, icon, type, sort_order, status) VALUES
('数据概览', 'dashboard', 0, '/dashboard', 'el-icon-s-data', 1, 1, 1),
('部门管理', 'department', 0, '/departments', 'el-icon-office-building', 1, 2, 1),
('人员管理', 'employee', 0, '/employees', 'el-icon-user-solid', 1, 3, 1),
('图书管理', 'book', 0, '/books', 'el-icon-reading', 1, 4, 1),
('分类管理', 'category', 0, '/categories', 'el-icon-folder-opened', 1, 5, 1),
('借阅管理', 'borrow', 0, '/borrow', 'el-icon-document', 1, 6, 1),
('用户管理', 'user', 0, '/users', 'el-icon-user', 1, 7, 1),
('新增部门', 'department:add', 2, '', '', 2, 1, 1),
('编辑部门', 'department:edit', 2, '', '', 2, 2, 1),
('删除部门', 'department:delete', 2, '', '', 2, 3, 1),
('新增人员', 'employee:add', 3, '', '', 2, 1, 1),
('编辑人员', 'employee:edit', 3, '', '', 2, 2, 1),
('删除人员', 'employee:delete', 3, '', '', 2, 3, 1),
('设置权限', 'employee:permission', 3, '', '', 2, 4, 1),
('新增图书', 'book:add', 4, '', '', 2, 1, 1),
('编辑图书', 'book:edit', 4, '', '', 2, 2, 1),
('删除图书', 'book:delete', 4, '', '', 2, 3, 1),
('新增分类', 'category:add', 5, '', '', 2, 1, 1),
('编辑分类', 'category:edit', 5, '', '', 2, 2, 1),
('删除分类', 'category:delete', 5, '', '', 2, 3, 1),
('新增借阅', 'borrow:add', 6, '', '', 2, 1, 1),
('编辑借阅', 'borrow:edit', 6, '', '', 2, 2, 1),
('删除借阅', 'borrow:delete', 6, '', '', 2, 3, 1),
('新增用户', 'user:add', 7, '', '', 2, 1, 1),
('编辑用户', 'user:edit', 7, '', '', 2, 2, 1),
('删除用户', 'user:delete', 7, '', '', 2, 3, 1);

-- 插入人员权限关联数据（给张三、李四分配权限）
INSERT INTO sys_employee_permission (employee_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20), (1, 21),
(1, 22), (1, 23), (1, 24), (1, 25), (1, 26),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6),
(2, 8), (2, 9), (2, 10), (2, 11), (2, 12), (2, 13), (2, 14),
(2, 15), (2, 16), (2, 17), (2, 18), (2, 19), (2, 20);
