package com.offer.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * @author rwz
 * @since 2025/2/6
 * 短链接回收站管理接口层
 */
public interface ShortLinkRecycleBinService {


    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 分页查询回收站短链接请求参数
     * @return 分页查询回收站数据返回
     */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
