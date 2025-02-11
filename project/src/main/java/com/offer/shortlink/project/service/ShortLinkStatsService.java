package com.offer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
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

    /**
     * 获取单个短链接访问记录数据
     *
     * @param requestParam 获取单个短链接访问记录数据请求对象
     * @return 分页结果
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);
}
