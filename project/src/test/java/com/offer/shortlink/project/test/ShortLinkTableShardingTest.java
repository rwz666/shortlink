package com.offer.shortlink.project.test;

/**
 * @author rwz
 * @since 2025/1/26
 */
public class ShortLinkTableShardingTest {

    private static String SQL = """
            CREATE TABLE `t_group_%s` (
              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
              `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
              `name` varchar(64) DEFAULT NULL COMMENT '分组名称',
              `username` varchar(255) DEFAULT NULL COMMENT '创建分组用户名',
              `sort_order` int DEFAULT NULL COMMENT '分组排序',
              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
              `update_time` datetime DEFAULT NULL COMMENT '修改时间',
              `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
              PRIMARY KEY (`id`),
              UNIQUE KEY `idx_unique_username_gid` (`username`,`gid`) USING BTREE
            ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """;


    public static void main(String[] args) {
        for (int i = 0; i < 15; i++) {
            System.out.printf(SQL + "\n%n", i);
        }
    }

}
