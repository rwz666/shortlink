package com.offer.shortlink.project.dto.resp;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/11
 * 短链接网络监控返回对象
 */
@Data
public class ShortLinkStatsNetworkRespDTO {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 网络类型
     */
    private String network;

    /**
     * 占比
     */
    private Double ratio;
}
