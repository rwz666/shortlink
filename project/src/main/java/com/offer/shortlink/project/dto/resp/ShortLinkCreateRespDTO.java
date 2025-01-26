package com.offer.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rwz
 * @since 2025/1/26
 * 短链接创建返回对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkCreateRespDTO {

    /**
     * 分组id
     */
    private String gid;

    /**
     * 原始连接
     */
    private String originUrl;

    /**
     * 短链接
     */
    private String fullShortUrl;
}
