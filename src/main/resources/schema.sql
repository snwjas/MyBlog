-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: myblogdb
-- ------------------------------------------------------
-- Server version	8.0.24

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attachment` (
                              `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '文件id',
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名（包括后缀）',
                              `size` bigint unsigned NOT NULL DEFAULT '0' COMMENT '文件大小（字节）',
                              `path` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件路径',
                              `media_type` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'application/octet-stream' COMMENT 'http资源类型',
                              `thumb_path` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '文件缩略图路径',
                              `team` varchar(255) DEFAULT '' COMMENT '文件分组',
                              `width` int unsigned DEFAULT '0' COMMENT '文件为图片时，图片的宽度像素',
                              `height` int unsigned DEFAULT '0' COMMENT '文件为图片时，图片的高度像素',
                              `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `blog`
--

DROP TABLE IF EXISTS `blog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog` (
                        `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '博客id',
                        `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '博客标题',
                        `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '博客链接',
                        `original_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '原格式博客内容',
                        `format_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '格式化(html)博客内容',
                        `summary` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '博客摘要',
                        `thumbnail` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '博客缩略图链接',
                        `top_rank` int DEFAULT '0' COMMENT '博客置顶排行',
                        `allow_comment` tinyint unsigned DEFAULT '1' COMMENT '0：不允许评论；1：允许评论（默认）',
                        `likes` int unsigned DEFAULT '0' COMMENT '喜欢的人数',
                        `dislikes` int unsigned DEFAULT '0' COMMENT '不喜欢的人数',
                        `visits` int unsigned DEFAULT '0' COMMENT '博客访问人数',
                        `status` tinyint unsigned DEFAULT NULL COMMENT '博客状态',
                        `category_id` int DEFAULT NULL COMMENT '博客分类ID',
                        `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `uniq_url` (`url`) USING BTREE COMMENT 'url唯一'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='博客文章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `blog_tag`
--

DROP TABLE IF EXISTS `blog_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_tag` (
                            `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '博客标签id',
                            `tag_id` int NOT NULL COMMENT '标签id',
                            `blog_id` int NOT NULL COMMENT '博客id',
                            `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='博客文章标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
                            `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '分类id',
                            `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名',
                            `parent_id` int unsigned DEFAULT '0' COMMENT '父分类id',
                            `description` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '分类描述',
                            `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
                           `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '评论id',
                           `parent_id` int DEFAULT '0' COMMENT '父评论id，首级评论为0',
                           `content` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
                           `author` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '评论作者',
                           `email` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '评论作者邮箱',
                           `avatar` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '评论作者头像',
                           `ip_address` int DEFAULT '0' COMMENT '评论作者的ipv4地址，整型',
                           `user_agent` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户代理',
                           `blog_id` int NOT NULL COMMENT '博客id',
                           `status` tinyint unsigned DEFAULT '0' COMMENT '评论状态',
                           `is_admin` tinyint unsigned DEFAULT '0' COMMENT '0: 访客（默认）；1：管理员',
                           `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='评论';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `link`
--

DROP TABLE IF EXISTS `link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `link` (
                        `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '友链id',
                        `name` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '友链名称',
                        `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '友链链接',
                        `logo` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '友链logo',
                        `top_rank` int DEFAULT '0' COMMENT '友链排行',
                        `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '友链描述',
                        `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='友链';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log` (
                       `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '日志id',
                       `content` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '操作内容',
                       `type` tinyint DEFAULT '0' COMMENT '操作类型',
                       `ip_address` int DEFAULT '0' COMMENT '操作人的ipv4地址，整型',
                       `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `options`
--

DROP TABLE IF EXISTS `options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options` (
                           `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '系统设置id',
                           `option_key` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '键',
                           `option_value` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '值',
                           `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统（博客）设置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `statistics`
--

DROP TABLE IF EXISTS `statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistics` (
                              `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '统计id',
                              `web_visit_count` int unsigned DEFAULT '0' COMMENT '网站访问量',
                              `blog_visit_count` int unsigned DEFAULT '0' COMMENT '文章访问量',
                              `comment_count` int unsigned DEFAULT '0' COMMENT '评论数量',
                              `date` timestamp NOT NULL COMMENT '统计日期',
                              `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='统计表（统计每日的数据）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
                       `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '标签id',
                       `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '标签名',
                       `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id',
                        `username` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名',
                        `password` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
                        `nickname` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
                        `email` varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户邮箱',
                        `avatar` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户头像链接',
                        `description` varchar(1023) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '个人描述',
                        `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;


INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `avatar`, `description`) VALUES ('admin', '$2y$05$1bJ0VWvrCAvwdOHgEk6pneNmptl4MGkpUjACmg0nKOV608L9VFjea', 'MylesYYY', 'myles.yang@foxmail.com', '', 'https://refrain.xyz/');

--
-- Table structure for table `speciallist`
--

DROP TABLE IF EXISTS `speciallist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `speciallist` (
                               `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `type` tinyint(4) unsigned NOT NULL COMMENT '类型',
                               `content` varchar(511) DEFAULT '' COMMENT '内容',
                               `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='特殊清单表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;


--
-- Dumping routines for database 'myblogdb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
