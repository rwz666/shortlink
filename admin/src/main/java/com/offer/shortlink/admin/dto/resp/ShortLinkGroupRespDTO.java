package com.offer.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/1/24
 * 短链接分组返回对象
 */
@Data
public class ShortLinkGroupRespDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
