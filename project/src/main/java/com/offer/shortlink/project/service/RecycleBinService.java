package com.offer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkPageRespDTO;

/**
 * @author rwz
 * @since 2025/2/6
 * 回收站管理接口层
 */
public interface RecycleBinService {

    /**
     * 新增回收站数据
     *
     * @param requestParam 新增回收站数据请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 分页查询回收站短链接请求参数,gid集合
     * @return 分页查询回收站数据返回
     */
    IPage<ShortLinkPageRespDTO> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
