package com.offer.shortlink.admin.test;

import cn.hutool.core.lang.UUID;
import org.junit.jupiter.api.Test;

/**
 * @author rwz
 * @since 2025/1/24
 */
public class UserServiceTest {

    @Test
    void testUUID(){
        String uuid = UUID.randomUUID(false).toString();
        System.out.println(uuid);
    }
}
