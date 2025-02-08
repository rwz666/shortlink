package com.offer.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

    /**
     * 获取请求真实IP
     * @param request 当前请求
     * @return 用户真实IP
     */
    public static String getActualIp(HttpServletRequest request) {
        // 1. 尝试从X-Forwarded-For获取
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            List<String> ips = Arrays.asList(xff.split(","));
            Optional<String> realIp = ips.stream()
                    .map(String::trim)
                    .filter(ip -> !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip))
                    .findFirst();
            if (realIp.isPresent()) {
                return realIp.get();
            }
        }

        // 2. 尝试从X-Real-IP获取
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp.trim();
        }

        // 3. 回退到remoteAddr
        return request.getRemoteAddr();
    }
}
