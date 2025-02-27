package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkOsStatsDO;
import com.offer.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

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
    @Insert("""
            INSERT INTO t_link_os_stats(full_short_url, date, os, cnt, create_time, update_time, del_flag)
            VALUES
                (#{bean.fullShortUrl}, #{bean.date}, #{bean.os}, #{bean.cnt}, NOW(), NOW(), 0)
            ON DUPLICATE KEY UPDATE
                cnt = cnt + #{bean.cnt}, update_time = now();
            """)
    void shortLinkOsStats(@Param("bean") LinkOsStatsDO linkOsStatsDO);

    /**
     * 操作系统访问详情统计
     *
     * @param requestParam 短链接请求参数
     * @return 操作系统类型和个数
     */
    @Select("""
            SELECT
            	tlos.os,
            	sum(tlos.cnt) as cnt
            FROM
                t_link tl INNER JOIN
            	t_link_os_stats tlos ON
                tl.full_short_url = tlos.full_short_url
            WHERE
                tlos.full_short_url = #{bean.fullShortUrl}
            AND tl.gid = #{bean.gid}
            AND tlos.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = #{bean.enableStatus}
            GROUP BY
            	tl.gid,
            	tlos.full_short_url,
            	tlos.os;
            """)
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 分组操作系统访问详情统计
     *
     * @param requestParam 分组短链接请求参数
     * @return 分组操作系统类型和个数
     */
    @Select("""
            SELECT
            	tlos.os,
            	sum(tlos.cnt) as cnt
            FROM
                t_link tl INNER JOIN
                t_link_os_stats tlos ON
                tl.full_short_url = tlos.full_short_url
            WHERE
                tl.gid = #{bean.gid}
            AND tlos.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = '0'
            GROUP BY
            	tl.gid,
            	tlos.os;
            """)
    List<HashMap<String, Object>> listOsStatsByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);
}
