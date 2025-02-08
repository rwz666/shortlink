package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkNetworkStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author rwz
 * @since 2025/2/8
 * 短链接访问设备数据统计持久层
 */
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {

    /**
     * 短链接访问网络数据统计
     *
     * @param linkNetworkStatsDO 短链接访问网络数据实体
     */
    @Insert(
            """
            INSERT INTO t_link_network_stats(full_short_url, gid, date, cnt, network, create_time, update_time, del_flag )
                VALUES
                    (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.cnt}, #{bean.network}, NOW(), NOW(),0)
                ON DUPLICATE KEY UPDATE
                    cnt = cnt + #{bean.cnt}, update_time = NOW();
            """)
    void shortLinkNetworkStats(@Param("bean") LinkNetworkStatsDO linkNetworkStatsDO);
}
