package com.offer.shortlink.admin.test;

/**
 * @author rwz
 * @since 2025/1/23
 */
public class UserTableShardingTest {

    private static final String SQL = """
            CREATE TABLE `t_user_%d` (
              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
              `username` varchar(255) DEFAULT NULL COMMENT '用户名',
              `password` varchar(512) DEFAULT NULL COMMENT '密码',
              `real_name` varchar(255) DEFAULT NULL COMMENT '真实姓名',
              `phone` varchar(128) DEFAULT NULL COMMENT '手机号',
              `mail` varchar(512) DEFAULT NULL COMMENT '邮箱',
              `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
              `update_time` datetime DEFAULT NULL COMMENT '修改时间',
              `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
              PRIMARY KEY (`id`),
              UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
            ) ENGINE=InnoDB AUTO_INCREMENT=1882372818550284291 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;""";

    public static void main(String[] args) {
        for (int i = 0 ; i < 16; i ++){
            System.out.printf((SQL) + "%n", i);
        }
    }


}
