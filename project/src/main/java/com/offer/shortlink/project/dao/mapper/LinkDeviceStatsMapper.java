package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkDeviceStatsDO;
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
 * 短链接访问设备数据统计持久层
 */
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {

    /**
     * 短链接访问设备数据统计
     *
     * @param linkDeviceStatsDO 短链接访问设备数据实体
     */
    @Insert("""
            INSERT INTO t_link_device_stats(full_short_url, date, cnt, device, create_time, update_time, del_flag)
                VALUES
                    (#{bean.fullShortUrl}, #{bean.date}, #{bean.cnt}, #{bean.device}, NOW(), NOW(), 0)
                ON DUPLICATE KEY UPDATE
                    cnt = cnt + #{bean.cnt}, update_time = NOW();
            """)
    void shortLinkDeviceStats(@Param("bean") LinkDeviceStatsDO linkDeviceStatsDO);

    /**
     * 根据短链接获取指定日期内访问设备监控数据
     */
    @Select("""
            SELECT
                tlds.device,
                sum(tlds.cnt)  as cnt
            FROM
                t_link tl INNER JOIN
                t_link_device_stats tlds ON
                tl.full_short_url = tlds.full_short_url
            WHERE
                tlds.full_short_url = #{bean.fullShortUrl}
            AND tl.gid = #{bean.gid}
            AND tlds.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = #{bean.enableStatus}
            GROUP BY
                tl.gid,
                tlds.full_short_url,
                tlds.device
            """)
    List<HashMap<String, Object>> listDeviceStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内访问设备监控数据
     */
    @Select("""
            SELECT
                tlds.device,
                sum(tlds.cnt)  as cnt
            FROM
                t_link tl INNER JOIN
                t_link_device_stats tlds ON
                tl.full_short_url = tlds.full_short_url
            WHERE
                tl.gid = #{bean.gid}
            AND tlds.`date` BETWEEN #{bean.startDate} AND #{bean.endDate}
            AND tl.del_flag = '0'
            AND tl.enable_status = '0'
            GROUP BY
                tl.gid,
                tlds.device
            """)
    List<HashMap<String, Object>> listDeviceStatsByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);
}
