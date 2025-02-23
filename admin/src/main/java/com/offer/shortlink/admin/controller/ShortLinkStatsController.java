package com.offer.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.remote.ShortLinkRemoteClient;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rwz
 * @since 2025/2/10
 * 短链接监控控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkRemoteClient shortLinkRemoteClient;

    /**
     * 获取单个短链接监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteClient.oneShortLinkStats(requestParam);
    }

    /**
     * 获取分组短链接监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return shortLinkRemoteClient.groupShortLinkStats(requestParam);
    }

    /**
     * 获取单个短链接访问记录数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteClient.shortLinkStatsAccessRecord(requestParam);
    }

    /**
     * 获取分组短链接访问记录数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteClient.groupShortLinkStatsAccessRecord(requestParam);
    }
}
