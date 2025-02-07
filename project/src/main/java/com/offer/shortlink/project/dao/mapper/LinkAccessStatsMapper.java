package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author rwz
 * @since 2025/2/7
 * 短链接访问数据统计持久层
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    @Insert("INSERT INTO t_link_access_stats ( full_short_url, gid, date, pv, uv, uip, HOUR, weekday, create_time, update_time, del_flag )\n" +
            "VALUES" +
            "( #{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.pv}, #{bean.uv}, #{bean.uip}, #{bean.hour}, #{bean.weekday}, NOW(), NOW(), 0)" +
            "ON DUPLICATE KEY UPDATE pv = pv + #{bean.pv}," +
            "uv = uv + #{bean.uv}," +
            "uip = uip + #{bean.uip};")
    void shortLinkStats(@Param("bean") LinkAccessStatsDO linkAccessStatsDO);
}
