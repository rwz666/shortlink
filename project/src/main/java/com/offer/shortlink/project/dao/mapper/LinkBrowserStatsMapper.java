package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author rwz
 * @since 2025/2/8
 * 短链接浏览器数据统计持久层
 */
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {

    /**
     * 短链接访问浏览器数据统计
     *
     * @param linkBrowserStatsDO 短链接访问基础数据实体
     */
    @Insert(
            """
            INSERT INTO t_link_browser_stats (full_short_url, gid, date, cnt, browser, create_time, update_time, del_flag )
                VALUES
                    (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.cnt}, #{bean.browser}, NOW(), NOW(),0)
                ON DUPLICATE KEY UPDATE
                    cnt = cnt + #{bean.cnt}, update_time = NOW();
            """)
    void shortLinkBrowserStats(@Param("bean") LinkBrowserStatsDO linkBrowserStatsDO);
}
