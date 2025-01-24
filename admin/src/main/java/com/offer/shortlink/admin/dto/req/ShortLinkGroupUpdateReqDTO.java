package com.offer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/1/25
 */
@Data
public class ShortLinkGroupUpdateReqDTO {

    /**
     * 分组ID
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;
}
