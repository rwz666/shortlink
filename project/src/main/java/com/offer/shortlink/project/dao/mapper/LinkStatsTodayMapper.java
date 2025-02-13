package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author rwz
 * @since 2025/2/12
 * 短链接今日访问持久层
 */
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {

    /**
     * 短链接今日访问监控
     *
     * @param linkStatsTodayDO 今日访问实体
     */
    @Insert("""
            INSERT INTO t_link_stats_today ( full_short_url, gid, date, today_pv, today_uv, today_uip, create_time, update_time, del_flag )
                VALUES
                    (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.todayPv}, #{bean.todayUv}, #{bean.todayUip}, NOW(), NOW(),0)
                ON DUPLICATE KEY UPDATE
                    today_pv = today_pv + #{bean.todayPv}, today_uv = today_uv + #{bean.todayUv}, today_uip = today_uip + #{bean.todayUip}, update_time = NOW();
            """)
    void shortLinkTodayStats(@Param("bean") LinkStatsTodayDO linkStatsTodayDO);
}
