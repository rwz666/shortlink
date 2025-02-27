package com.offer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.offer.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.offer.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author rwz
 * @since 2025/2/9
 * 短链接访问日志监控持久层
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    /**
     * 高频IP访问统计（前五）
     *
     * @param requestParam 高频IP访问统计请求参数
     */
    List<HashMap<String, Object>> listTop5IpByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内新老用户访客数据
     */
    HashMap<String, Object> findUvTypeCntByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 获取用户在指定时间段是新用户还是老用户
     */
    List<HashMap<String, Object>> selectUvTypeByUsers(
            @Param("gid") String gid,
            @Param("fullShortUrl") String fullShortUrl,
            @Param("enableStatus") Integer enableStatus,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userSetList") List<String> userSetList);

    /**
     * 获取分组用户在指定时间段是新用户还是老用户
     */
    List<HashMap<String, Object>> selectGroupUvTypeByUsers(
            @Param("gid") String gid,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userSetList") List<String> userSetList);


    /**
     * 根据日志表查询PvUvUip数据
     */
    LinkAccessStatsDO findPvUvUipStatsByShortLink(@Param("bean") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内新老用户访客数据
     */
    LinkAccessStatsDO findPvUvUipStatsByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 分组高频IP访问统计（前五）
     *
     * @param requestParam 分组高频IP访问统计请求参数
     */
    List<HashMap<String, Object>> listTop5IpByGroup(@Param("bean") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 分页查询分组
     */
    IPage<LinkAccessLogsDO> selectGroupPage(@Param("bean") ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
