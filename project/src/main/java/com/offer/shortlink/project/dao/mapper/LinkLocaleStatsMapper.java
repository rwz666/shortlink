package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offer.shortlink.project.dao.entity.LinkLocaleStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

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
    @Insert(
    """
    INSERT INTO t_link_locale_stats(full_short_url, gid, date, province, city, adcode, cnt, country, create_time, update_time, del_flag)
        VALUES
            (#{bean.fullShortUrl}, #{bean.gid}, #{bean.date}, #{bean.province}, #{bean.city}, #{bean.adcode}, #{bean.cnt}, #{bean.country}, NOW(), NOW(), 0)
        ON DUPLICATE KEY UPDATE
            cnt = cnt + #{bean.cnt}, update_time = now();
    """)
    void shortLinkLocaleStats(@Param("bean") LinkLocaleStatsDO linkLocaleStatsDO);

}
