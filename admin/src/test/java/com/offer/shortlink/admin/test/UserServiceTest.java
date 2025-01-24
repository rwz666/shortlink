package com.offer.shortlink.admin.test;

import cn.hutool.core.lang.UUID;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

    @Test
    void testTransformToUrlEncoding(){
        try {
            String s = URLEncoder.encode("贰三四1", "UTF-8");
            System.out.println(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
