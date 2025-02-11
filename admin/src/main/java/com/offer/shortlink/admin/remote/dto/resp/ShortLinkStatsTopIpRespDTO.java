package com.offer.shortlink.admin.remote.dto.resp;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/11
 * 短链接监控高频IP返回对象
 */
@Data
public class ShortLinkStatsTopIpRespDTO {

    /**
     * ip
     */
    private String ip;

    /**
     * 统计
     */
    private int cnt;
}
