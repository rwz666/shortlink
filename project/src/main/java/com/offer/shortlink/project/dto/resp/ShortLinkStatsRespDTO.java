package com.offer.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author rwz
 * @since 2025/2/9
 * 短链接监控返回对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkStatsRespDTO {

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立访客数
     */
    private Integer uv;

    /**
     * 独立IP数
     */
    private Integer uip;

    /**
     * 每日基础访问状态
     */
    private List<ShortLinkStatsDailyRespDTO> daily;

    /**
     * 地区访问详情（国内）
     */
    private List<ShortLinkStatsLocaleCNTop10RespDTO> localeCnStats;

    /**
     * 24小时访问数据
     */
    private List<Integer> hourStats;

    /**
     * 高频IP访问详情
     */
    private List<ShortLinkStatsTopIpRespDTO> topIpStats;

    /**
     * 一周访问详情
     */
    private List<Integer> weekdayStats;

    /**
     * 操作系统访问详情
     */
    private List<ShortLinkStatsOsRespDTO> osStats;

    /**
     * 浏览器访问详情
     */
    private List<ShortLinkStatsBrowserRespDTO> browserStats;


    /**
     * 访客访问类型详情
     */
    private List<ShortLinkStatsUvRespDTO> uvTypeStats;

    /**
     * 访问网络类型详情
     */
    private List<ShortLinkStatsNetworkRespDTO> networkStats;

    /**
     * 访问设备类型详情
     */
    private List<ShortLinkStatsDeviceRespDTO> deviceStats;

}
