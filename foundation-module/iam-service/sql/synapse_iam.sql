/*
 Navicat Premium Dump SQL

 Source Server         : 10.80.3.24
 Source Server Type    : MySQL
 Source Server Version : 80407 (8.4.7-router)
 Source Host           : 10.80.3.24:6450
 Source Schema         : synapse_iam

 Target Server Type    : MySQL
 Target Server Version : 80407 (8.4.7-router)
 File Encoding         : 65001

 Date: 08/11/2025 16:25:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for iam_menu
-- ----------------------------
DROP TABLE IF EXISTS `iam_menu`;
CREATE TABLE `iam_menu` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `system_id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统Id',
  `parent_id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '父Id',
  `code` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单编码 系统唯一',
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单名称',
  `icon` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图标',
  `router` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '静态路由地址',
  `component` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '静态组件地址',
  `status` tinyint DEFAULT '1' COMMENT '状态 0:失效、1:生效',
  `visible` tinyint DEFAULT '1' COMMENT '是否可见 0:否、1:是',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `revision` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `deleted` tinyint NOT NULL DEFAULT '1' COMMENT '软删标识 1:未删除、0:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单表';

-- ----------------------------
-- Records of iam_menu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for iam_resources
-- ----------------------------
DROP TABLE IF EXISTS `iam_resources`;
CREATE TABLE `iam_resources` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `system_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统Id',
  `menu_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单Id 只有FUNCTION存在',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型 API、 DATA、 \nFUNCTION：前端按钮',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源描述',
  `permissions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `revision` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `deleted` tinyint NOT NULL DEFAULT '1' COMMENT '软删标识 1:未删除、0:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资源表';

-- ----------------------------
-- Records of iam_resources
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for iam_role
-- ----------------------------
DROP TABLE IF EXISTS `iam_role`;
CREATE TABLE `iam_role` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `code` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0:停用、1:启用',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `revision` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `deleted` tinyint NOT NULL DEFAULT '1' COMMENT '软删标识 1:未删除、0:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ----------------------------
-- Records of iam_role
-- ----------------------------
BEGIN;
INSERT INTO `iam_role` (`id`, `code`, `description`, `status`, `create_user`, `create_time`, `modify_user`, `modify_time`, `revision`, `deleted`) VALUES ('1986713754259144705', 'admin', '管理员', 1, 'system', '2025-11-07 16:34:05', 'system', '2025-11-07 16:34:05', 1, 1);
INSERT INTO `iam_role` (`id`, `code`, `description`, `status`, `create_user`, `create_time`, `modify_user`, `modify_time`, `revision`, `deleted`) VALUES ('1986713847968284674', 'WMS', '仓管员', 1, 'system', '2025-11-07 16:34:27', 'system', '2025-11-07 16:34:27', 1, 1);
INSERT INTO `iam_role` (`id`, `code`, `description`, `status`, `create_user`, `create_time`, `modify_user`, `modify_time`, `revision`, `deleted`) VALUES ('1986713900673908737', 'MES', '生产', 1, 'system', '2025-11-07 16:34:40', 'system', '2025-11-07 16:34:40', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for iam_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `iam_role_menu`;
CREATE TABLE `iam_role_menu` (
  `id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `role_id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色Id',
  `menu_id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单Id',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单表';

-- ----------------------------
-- Records of iam_role_menu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for iam_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `iam_role_resource`;
CREATE TABLE `iam_role_resource` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `role_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色Id',
  `resource_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源Id',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色资源表';

-- ----------------------------
-- Records of iam_role_resource
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for iam_role_system（已暂时注释）
-- 暂时取消 iam_role_system 表，通过菜单推导系统关系
-- 系统关系可以通过 role_menu -> menu -> system_id 推导
-- ----------------------------
-- DROP TABLE IF EXISTS `iam_role_system`;
-- CREATE TABLE `iam_role_system` (
--   `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
--   `role_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色Id',
--   `system_id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统Id',
--   `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
--   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
--   PRIMARY KEY (`id`),
--   UNIQUE KEY `uk_role_system` (`role_id`, `system_id`) COMMENT '角色系统唯一索引'
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色系统关联表';
--
-- -- ----------------------------
-- -- Records of iam_role_system
-- -- ----------------------------
-- BEGIN;
-- COMMIT;

-- ----------------------------
-- Table structure for iam_system
-- ----------------------------
DROP TABLE IF EXISTS `iam_system`;
CREATE TABLE `iam_system` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `code` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '系统编码',
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '系统名称',
  `status` tinyint DEFAULT NULL COMMENT '状态 0:停用、1:启用',
  `sorted` int DEFAULT NULL COMMENT '排序',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `revision` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `deleted` tinyint NOT NULL DEFAULT '1' COMMENT '软删标识 1:未删除、0:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统表';

-- ----------------------------
-- Records of iam_system
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for iam_user
-- ----------------------------
DROP TABLE IF EXISTS `iam_user`;
CREATE TABLE `iam_user` (
  `id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `account` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电子邮件',
  `mobile` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系方式',
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实名称',
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像地址',
  `type` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户类型 INNER 内部账号，SUPPLIER: 供应商，CUSTOMER 客户账号',
  `locked` tinyint NOT NULL DEFAULT '0' COMMENT '是否锁定 0:否、1:是',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用 0:否、1：是',
  `expired` tinyint(3) unsigned zerofill DEFAULT '000' COMMENT '是否过期 0:否、1:是',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `revision` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `deleted` tinyint NOT NULL DEFAULT '1' COMMENT '软删标识 1:未删除、0:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- Records of iam_user
-- ----------------------------
BEGIN;
INSERT INTO `iam_user` (`id`, `account`, `password`, `email`, `mobile`, `real_name`, `avatar`, `type`, `locked`, `enabled`, `expired`, `create_user`, `create_time`, `modify_user`, `modify_time`, `revision`, `deleted`) VALUES ('1986686516532805633', 'admin', '$2a$10$68LOvFg5Z4oXpe1XBeebBufoIZdErwwbSVALOHnpQXMM06uXb9UJ6', 'christ.sxc@gmail.com', '12314772745', 'sxc', NULL, 'INNER', 0, 1, 000, 'system', '2025-11-07 14:45:51', 'system', '2025-11-07 14:45:51', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for iam_user_role
-- ----------------------------
DROP TABLE IF EXISTS `iam_user_role`;
CREATE TABLE `iam_user_role` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `user_id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户Id',
  `role_id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色Id',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Records of iam_user_role
-- ----------------------------
BEGIN;
INSERT INTO `iam_user_role` (`id`, `user_id`, `role_id`, `create_user`, `create_time`) VALUES ('1986716589914722306', '1986686516532805633', '1986713754259144705', 'system', '2025-11-07 16:45:21');
INSERT INTO `iam_user_role` (`id`, `user_id`, `role_id`, `create_user`, `create_time`) VALUES ('1986716589923110913', '1986686516532805633', '1986713847968284674', 'system', '2025-11-07 16:45:21');
INSERT INTO `iam_user_role` (`id`, `user_id`, `role_id`, `create_user`, `create_time`) VALUES ('1986716589923110914', '1986686516532805633', '1986713900673908737', 'system', '2025-11-07 16:45:21');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
