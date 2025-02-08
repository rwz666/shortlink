package com.offer.shortlink.project.common.constant;

/**
 * @author rwz
 * @since 2025/2/5
 * 短链接常量类
 */
public class ShortLinkConstant {

    /**
     * 永久短链接默认过期时间
     */
    public static long DEFAULT_CACHE_VALID_TIME = 2592000000L;

    /**
     * 高德api 根据ip获取地理位置
     */
    public static String AMAP_REMOTE_GET_LOCALE_BY_IP_URL = "https://restapi.amap.com/v3/ip";

}
