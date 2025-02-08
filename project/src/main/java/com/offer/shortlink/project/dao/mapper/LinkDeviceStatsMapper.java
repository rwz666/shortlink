package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkDeviceStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

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
    @Insert(
            """
            INSERT INTO t_link_device_stats(full_short_url, gid, date, cnt, device, create_time, update_time, del_flag )
                VALUES
                    (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.cnt}, #{bean.device}, NOW(), NOW(),0)
                ON DUPLICATE KEY UPDATE
                    cnt = cnt + #{bean.cnt}, update_time = NOW();
            """)
    void shortLinkDeviceStats(@Param("bean") LinkDeviceStatsDO linkDeviceStatsDO);
}
