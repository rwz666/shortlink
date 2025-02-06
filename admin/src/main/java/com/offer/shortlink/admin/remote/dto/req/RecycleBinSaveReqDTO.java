package com.offer.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/6
 * 回收站新增数据实体
 */
@Data
public class RecycleBinSaveReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

}
