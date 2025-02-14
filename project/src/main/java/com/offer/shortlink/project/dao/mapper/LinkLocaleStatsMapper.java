package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkLocaleStatsDO;
import com.offer.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkStatsLocaleCNTop10RespDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author rwz
 * @since 2025/2/8
 * 短链接访问地区数据统计持久层
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    /**
     * 短链接访问地区数据统计
     *
     * @param linkLocaleStatsDO 短链接访问地区数据实体
     */
    @Insert("""
            INSERT INTO t_link_locale_stats(full_short_url, gid, date, province, city, adcode, cnt, country, create_time, update_time, del_flag)
                VALUES
                    (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.province}, #{bean.city}, #{bean.adcode}, #{bean.cnt}, #{bean.country}, NOW(), NOW(), 0)
                ON DUPLICATE KEY UPDATE
                    cnt = cnt + #{bean.cnt}, update_time = now();
            """)
    void shortLinkLocaleStats(@Param("bean") LinkLocaleStatsDO linkLocaleStatsDO);

    /**
     * 短链接访问地区数据前10统计
     *
     * @param requestParam 短链接访问地区数据前10统计请求参数
     * @return 各个省份的数据个数
     */
    @Select("""
            SELECT
                SUM(cnt) as cnt,
                province AS locale
            FROM
                t_link_locale_stats
            WHERE
                full_short_url = #{bean.fullShortUrl}
            AND gid = #{bean.gid}
            AND `date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND del_flag = '0'
            GROUP BY full_short_url, gid, province
            LIMIT 10;
            """)
    List<ShortLinkStatsLocaleCNTop10RespDTO> listLocaleTop10ByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 分组访问地区数据前10统计
     *
     * @param requestParam 短链接访问地区数据前10统计请求参数
     * @return 各个省份的数据个数
     */
    @Select("""
            SELECT
                SUM(cnt) as cnt,
                province AS locale
            FROM
                t_link_locale_stats
            WHERE
                gid = #{bean.gid}
            AND `date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND del_flag = '0'
            GROUP BY gid, province
            LIMIT 10;
            """)
    List<ShortLinkStatsLocaleCNTop10RespDTO> listLocaleTop10ByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);
}
