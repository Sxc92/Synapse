/*
 Navicat Premium Dump SQL

 Source Server         : local_Mysql
 Source Server Type    : MySQL
 Source Server Version : 80402 (8.4.2)
 Source Host           : localhost:3306
 Source Schema         : synapse_mdm

 Target Server Type    : MySQL
 Target Server Version : 80402 (8.4.2)
 File Encoding         : 65001

 Date: 27/09/2025 09:05:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for mdm_country
-- ----------------------------
DROP TABLE IF EXISTS `mdm_country`;
CREATE TABLE `mdm_country` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '国家编码',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '国家名称',
  `enabled` tinyint DEFAULT '1' COMMENT '启用状态 0:停用、1:启用',
  `create_user` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint DEFAULT '1' COMMENT '软删标识 0:否、1:是',
  `revision` int DEFAULT '1' COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='国家表';

-- ----------------------------
-- Records of mdm_country
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mdm_i18n
-- ----------------------------
DROP TABLE IF EXISTS `mdm_i18n`;
CREATE TABLE `mdm_i18n` (
  `id` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `lang_code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '语言编码',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'msg key',
  `country_code` varchar(16) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '国家编码',
  `context` text COLLATE utf8mb4_general_ci COMMENT '内容',
  `type` tinyint DEFAULT NULL COMMENT '类型 0:error、1:UI、2: other',
  `create_user` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint DEFAULT '1' COMMENT '软删标识 0:否、1:是',
  `revision` int DEFAULT '1' COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='国际化内容表';

-- ----------------------------
-- Records of mdm_i18n
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mdm_language
-- ----------------------------
DROP TABLE IF EXISTS `mdm_language`;
CREATE TABLE `mdm_language` (
  `id` varchar(19) COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `code` varchar(16) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '语言编码',
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '语言名称',
  `sys_default` tinyint DEFAULT '0' COMMENT '默认系统 0:否、1:是',
  `enabed` tinyint DEFAULT '1' COMMENT '状态 0:停用、1:启用',
  `create_user` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint DEFAULT '1' COMMENT '软删标识 0:否、1:是',
  `revision` int DEFAULT '1' COMMENT '乐观锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='语言表';

-- ----------------------------
-- Records of mdm_language
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime NOT NULL COMMENT 'create datetime',
  `log_modified` datetime NOT NULL COMMENT 'modify datetime',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of undo_log
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
