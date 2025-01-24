package com.offer.shortlink.admin.toolkit;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author rwz
 * @since 2025/1/24
 */
public class RandomStringUtil {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Random random = new SecureRandom();
    private static final int STRING_LENGTH = 6;

    /**
     * 生成长度为6的随机字符串
     *
     * @return 长度为6的字符串
     */
    public static String generateRandom() {
        return generateRandom(STRING_LENGTH);
    }

    /**
     * 生成随机长度字符串
     *
     * @param length 字符串长度
     * @return 随即长度字符串
     */
    public static String generateRandom(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
}
