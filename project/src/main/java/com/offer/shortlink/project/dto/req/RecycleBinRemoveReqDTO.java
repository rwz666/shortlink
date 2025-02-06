package com.offer.shortlink.project.dto.req;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/6
 * 回收站回复数据实体
 */
@Data
public class RecycleBinRemoveReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

}
