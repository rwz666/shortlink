package com.offer.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.remote.ShortLinkRemoteClient;
import com.offer.shortlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.offer.shortlink.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import com.offer.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.offer.shortlink.admin.service.ShortLinkRecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rwz
 * @since 2025/2/6
 * 回收站管理控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final ShortLinkRemoteClient shortLinkRemoteClient;

    private final ShortLinkRecycleBinService shortLinkRecycleBinService;

    /**
     * 新增回收站数据
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        shortLinkRemoteClient.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam){
        return shortLinkRecycleBinService.pageRecycleBinShortLink(requestParam);
    }

    /**
     * 恢复回收站短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        shortLinkRemoteClient.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 移除短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam){
        shortLinkRemoteClient.removeRecycleBin(requestParam);
        return Results.success();
    }
}
