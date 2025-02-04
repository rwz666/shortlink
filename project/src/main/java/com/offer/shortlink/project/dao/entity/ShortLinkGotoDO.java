package com.offer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rwz
 * @since 2025/2/4
 * 短链接跳转实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_goto")
public class ShortLinkGotoDO {

    /**
     * ID
     */
    private Long id;

    /**
     * 分组表示
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
