package com.offer.shortlink.project.service;

import com.offer.shortlink.project.dto.req.RecycleBinSaveReqDTO;

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

}
