package com.offer.shortlink.project.service;

import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * @author rwz
 * @since 2025/2/9
 * 短链接监控
 */
public interface ShortLinkStatsService {

    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 短链接监控数据请求参数
     * @return 短链接监控数据返回对象
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);
}
