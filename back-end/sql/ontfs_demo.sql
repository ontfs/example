
CREATE DATABASE ontfs_demo;
use ontfs_demo;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tbl_user
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user`;
CREATE TABLE `tbl_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ont_id` varchar(255) DEFAULT NULL COMMENT 'ontId',
  `user_name` varchar(191) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name` (`user_name`),
  KEY `idx_ont_id` (`ont_id`(191)),
  KEY `idx_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for tbl_ont_file
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ont_file`;
CREATE TABLE `tbl_ont_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ont_id` varchar(255) DEFAULT NULL COMMENT 'ontId',
  `file_name` text COMMENT '文件名',
  `file_hash` varchar(255) DEFAULT NULL COMMENT '文件hash',
  `tx_hash` varchar(255) DEFAULT NULL COMMENT '交易hash',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`),
  KEY `idx_ont_id` (`ont_id`(191)),
  KEY `idx_file_name` (`file_name`(191)) USING BTREE,
  KEY `idx_file_hash` (`file_hash`(191))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;
