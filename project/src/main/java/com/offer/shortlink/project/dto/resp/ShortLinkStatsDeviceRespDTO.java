package com.offer.shortlink.project.dto.resp;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/11
 * 短链接访问设备监控响应参数
 */
@Data
public class ShortLinkStatsDeviceRespDTO {

    /**
     * 统计
     */
    private Integer cnt;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 占比
     */
    private Double ratio;
}
