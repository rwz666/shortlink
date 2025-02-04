package com.offer.shortlink.project.test;

/**
 * @author rwz
 * @since 2025/1/26
 */
public class ShortLinkTableShardingTest {

    private static String SQL = """
            CREATE TABLE `t_link_goto_%d` (
              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
              `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
              `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """;

    private static String DROP_TABLE = "DROP TABLE `t_link_goto_%d`;";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf(DROP_TABLE + "\n%n", i);
        }
        for (int i = 0; i < 16; i++) {
            System.out.printf(SQL + "\n%n", i);
        }
    }

}
