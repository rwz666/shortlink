package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author rwz
 * @since 2025/2/8
 * 短链接访问操作系统数据统计持久层
 */
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    /**
     * 短链接访问操作系统数据统计
     *
     * @param linkOsStatsDO 短链接访问操作系统实体
     */
    @Insert(
    """
    INSERT INTO t_link_os_stats(full_short_url, gid, date, os, cnt, create_time, update_time, del_flag)
        VALUES
            (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.os}, #{bean.cnt}, NOW(), NOW(), 0)
        ON DUPLICATE KEY UPDATE
            cnt = cnt + #{bean.cnt}, update_time = now();
    """)
    void shortLinkOsStats(@Param("bean") LinkOsStatsDO linkOsStatsDO);
}
