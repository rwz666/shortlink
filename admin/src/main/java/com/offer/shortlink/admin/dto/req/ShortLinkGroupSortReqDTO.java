package com.offer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/1/25
 */
@Data
public class ShortLinkGroupSortReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 排序标识
     */
    private Integer sortOrder;
}
