package com.offer.shortlink.project.dto.resp;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/1/31
 */
@Data
public class ShortLinkGroupCountQueryRespDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组短连接数量
     */
    private Integer shortLinkCount;
}
