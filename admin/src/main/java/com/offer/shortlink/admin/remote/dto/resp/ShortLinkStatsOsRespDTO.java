package com.offer.shortlink.admin.remote.dto.resp;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/11
 * 操作系统访问详情
 */
@Data
public class ShortLinkStatsOsRespDTO {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 占比
     */
    private Double ratio;
}
