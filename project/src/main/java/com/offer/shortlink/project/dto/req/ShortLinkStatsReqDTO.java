package com.offer.shortlink.project.dto.req;

import lombok.Data;

import java.util.Date;

/**
 * @author rwz
 * @since 2025/2/9
 * 短链接监控请求对象
 */
@Data
public class ShortLinkStatsReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 启用状态
     */
    private Integer enableStatus;
}
