package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.offer.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
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
            INSERT INTO t_link_access_stats (full_short_url, date, pv, uv, uip, HOUR, weekday, create_time, update_time, del_flag)
                VALUES
                    (#{bean.fullShortUrl}, #{bean.date}, #{bean.pv}, #{bean.uv}, #{bean.uip}, #{bean.hour}, #{bean.weekday}, NOW(), NOW(),0)
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
                tlas.`date`,
                SUM(tlas.pv) AS pv,
                SUM(tlas.uv) AS uv,
                SUM(tlas.uip) AS uip
            FROM
                t_link tl INNER JOIN
                t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url
            WHERE
                tlas.full_short_url = #{bean.fullShortUrl}
            AND tl.gid = #{bean.gid}
            AND tlas.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = #{bean.enableStatus}
            GROUP BY
                tlas.full_short_url,
                tl.gid,
                tlas.`date`;
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
            	sum(tlas.pv) AS pv,
            	tlas.`hour`
            FROM
                t_link tl
            	INNER JOIN t_link_access_stats tlas
                ON tl.full_short_url = tlas.full_short_url
            WHERE
                tlas.full_short_url = #{bean.fullShortUrl}
            AND tl.gid = #{bean.gid}
            AND tlas.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = #{bean.enableStatus}
            GROUP BY
            	tl.gid,
            	tlas.full_short_url,
            	tlas.`hour`;
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
            	tlas.weekday,
            	sum(tlas.pv) AS pv
            FROM
                t_link tl
            	INNER JOIN t_link_access_stats tlas
                ON tl.full_short_url = tlas.full_short_url
            WHERE
                tlas.full_short_url = #{bean.fullShortUrl}
            AND tl.gid = #{bean.gid}
            AND tlas.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = #{bean.enableStatus}
            GROUP BY
            	tl.gid,
            	tlas.full_short_url,
            	tlas.weekday;
            """)
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 分组小时访问详情
     *
     * @param requestParam 分组小时访问详情请求对象
     * @return 分组小时访问详情返回
     */
    @Select("""
            SELECT
             sum(tlas.pv) AS pv,
             tlas.`hour`
            FROM
                t_link tl INNER JOIN
                t_link_access_stats tlas ON
                tl.full_short_url = tlas.full_short_url
            WHERE
                tl.gid = #{bean.gid}
            AND tlas.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = '0'
            GROUP BY
                tl.gid,
                tlas.`hour`;
            """)
    List<LinkAccessStatsDO> listHourStatsByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 分组每周访问详情
     *
     * @param requestParam 分组短链接请求参数
     * @return 分组短链接基础对象
     */
    @Select("""
            SELECT
            	tlas.weekday,
            	sum(tlas.pv) AS pv
            FROM
                t_link tl INNER JOIN
                t_link_access_stats tlas ON
                tl.full_short_url = tlas.full_short_url
            WHERE
                tl.gid = #{bean.gid}
            AND tlas.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = '0'
            GROUP BY
            	tl.gid,
            	tlas.weekday;
            """)
    List<LinkAccessStatsDO> listWeekdayStatsByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 分组基础访问详情
     *
     * @param requestParam 分组基础访问详情请求参数
     * @return 分组基础访问详情
     */
    @Select("""
            SELECT
                tlas.`date`,
                SUM(tlas.pv) AS pv,
                SUM(tlas.uv) AS uv,
                SUM(tlas.uip) AS uip
            FROM
                t_link tl INNER JOIN
                t_link_access_stats tlas ON
                tl.full_short_url = tlas.full_short_url
            WHERE
                tl.gid = #{bean.gid}
            AND tlas.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = '0'
            GROUP BY
                tlas.full_short_url,
                tl.gid,
                tlas.`date`;
            """)
    List<LinkAccessStatsDO> linkStatsByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);
}
