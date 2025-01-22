package com.offer.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author rwz
 * @since 2025/1/22
 */
@MapperScan("com.offer.shortlink.admin.dao.mapper")
@SpringBootApplication
public class ShortLinkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class);
    }
}
