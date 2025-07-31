-- 主库表结构
CREATE TABLE IF NOT EXISTS indigo.test_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用户表';

-- 从库表结构
CREATE TABLE IF NOT EXISTS indigo_slave.test_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试用户表';

-- 插入一些测试数据
INSERT INTO indigo.test_user (username, password, email) VALUES 
('test1', 'password1', 'test1@example.com'),
('test2', 'password2', 'test2@example.com');

INSERT INTO indigo_slave.test_user (username, password, email) VALUES 
('test1', 'password1', 'test1@example.com'),
('test2', 'password2', 'test2@example.com');

-- 创建测试表
CREATE TABLE IF NOT EXISTS test_table (
    id INT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试表';

-- 清理测试数据
DELETE FROM test_table; 