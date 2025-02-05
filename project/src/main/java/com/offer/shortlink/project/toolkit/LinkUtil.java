package com.offer.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.offer.shortlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;

/**
 * @author rwz
 * @since 2025/2/5
 * 短链接工具类
 */
public class LinkUtil {

    /**
     * 获取短链接缓存有效期
     *
     * @param validDate 有效期结束时间
     * @return 有效期时间戳
     */
    public static long getLinkCacheValidDate(Date validDate) {
        return Optional.ofNullable(validDate)
                .map(item -> DateUtil.between(new Date(), validDate, DateUnit.MS))
                .orElse(DEFAULT_CACHE_VALID_TIME);
    }
}
