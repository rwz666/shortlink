package com.offer.shortlink.project.test;

/**
 * @author rwz
 * @since 2025/1/26
 */
public class ShortLinkTableShardingTest {

    private static String SQL = """
            CREATE TABLE `t_link_%s` (
            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '短链接',
            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
            `click_num` int DEFAULT NULL COMMENT '点击量',
            `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'default' COMMENT '分组标识',
            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：启用 1：未启用',
            `create_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
            `valid_date_type` tinyint DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：自定义',
            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
            `describe` varchar(1024) DEFAULT NULL COMMENT '描述',
            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
            PRIMARY KEY (`id`),
            UNIQUE KEY `idx_unique_full_short-url` (`full_short_url`) USING BTREE
            ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """;


    public static void main(String[] args) {
        for (int i = 0; i < 15; i++) {
            System.out.printf(SQL + "\n%n", i);
        }
    }

}
