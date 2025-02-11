package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 短链接浏览器数据详情
     * @param requestParam 短链接访问基础数据实体
     */
    @Select("""
            SELECT
            	browser,
             	sum(cnt) as cnt
            FROM
            	t_link_browser_stats
            WHERE
                full_short_url = #{bean.fullShortUrl}
            AND gid = #{bean.gid}
            AND `date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            GROUP BY
            	gid,
             	full_short_url,
             	browser;
            """)
    List<HashMap<String, Object>> listBrowserStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);
}
