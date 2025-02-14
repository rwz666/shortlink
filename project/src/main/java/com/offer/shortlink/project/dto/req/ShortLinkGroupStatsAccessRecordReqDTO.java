package com.offer.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.offer.shortlink.project.dao.entity.LinkAccessLogsDO;
import lombok.Data;

/**
 * @author rwz
 * @since 2025/2/11
 * 单个短链接访问记录请求对象
 */
@Data
public class ShortLinkGroupStatsAccessRecordReqDTO extends Page<LinkAccessLogsDO> {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
