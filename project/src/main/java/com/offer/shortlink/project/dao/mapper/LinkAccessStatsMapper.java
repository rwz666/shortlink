package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author rwz
 * @since 2025/2/7
 * 短链接访问数据统计持久层
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 短链接访问基础数据统计
     *
     * @param linkAccessStatsDO 短链接访问基础数据实体
     */
    @Insert("""
            INSERT INTO t_link_access_stats ( full_short_url, gid, date, pv, uv, uip, HOUR, weekday, create_time, update_time, del_flag )
                VALUES
                    (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.pv}, #{bean.uv}, #{bean.uip}, #{bean.hour}, #{bean.weekday}, NOW(), NOW(),0)
                ON DUPLICATE KEY UPDATE
                    pv = pv + #{bean.pv}, uv = uv + #{bean.uv}, uip = uip + #{bean.uip}, update_time = NOW();
            """)
    void shortLinkStats(@Param("bean") LinkAccessStatsDO linkAccessStatsDO);

    /**
     * 每日基础访问详情
     *
     * @param shortLinkStatsReqDTO 每日基础访问详情请求参数
     * @return 每日基础访问详情
     */
    @Select("""
            SELECT
                `date`,
                SUM(pv) AS pv,
                SUM(uv) AS uv,
                SUM(uip) AS uip
            FROM
                t_link_access_stats
            WHERE
                full_short_url = #{bean.fullShortUrl}
            AND gid = #{bean.gid}
            AND `date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            GROUP BY full_short_url, gid, `date`;
            """)
    List<LinkAccessStatsDO> linkStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO shortLinkStatsReqDTO);

    /**
     * 小时访问详情
     *
     * @param requestParam 小时访问详情请求对象
     * @return 小时访问详情返回
     */
    @Select("""
            SELECT
            	sum(pv) AS pv,
            	`hour`
            FROM
            	t_link_access_stats
            WHERE
                full_short_url = #{bean.fullShortUrl}
            AND gid = #{bean.gid}
            AND `date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            GROUP BY
            	gid,
            	full_short_url,
            	`hour`;
            """)
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 每周访问详情
     *
     * @param requestParam 短链接请求参数
     * @return 短链接基础对象
     */
    @Select("""
            SELECT
            	weekday,
            	sum(pv) AS pv
            FROM
            	t_link_access_stats
            WHERE
                full_short_url = #{bean.fullShortUrl}
            AND gid = #{bean.gid}
            AND `date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            GROUP BY
            	gid,
            	full_short_url,
            	weekday;
            """)
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);
}
