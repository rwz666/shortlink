package com.offer.shortlink.project.dto.req;

import lombok.Data;

import java.util.Date;

/**
 * @author rwz
 * @since 2025/2/13
 * 分组短链接监控请求对象
 */
@Data
public class ShortLinkGroupStatsReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;
}
