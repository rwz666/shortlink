package com.offer.shortlink.project.dto.resp;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/11
 * 短链接监控中国地区top10返回队形
 */
@Data
public class ShortLinkStatsLocaleCNTop10RespDTO {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 地区
     */
    private String locale;

    /**
     * 占比
     */
    private Double ratio;
}
