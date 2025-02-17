package com.offer.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.remote.ShortLinkRemoteService;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.offer.shortlink.admin.toolkit.EasyExcelWebUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rwz
 * @since 2025/1/30
 */
@RestController
public class ShortLinkController {

    /**
     * 后续重构为SpringCloud Feign调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response){
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTO = shortLinkRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTO.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTO.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "短链接系统-"+System.currentTimeMillis(), ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return shortLinkRemoteService.pageShortLink(requestParam);
    }
}
