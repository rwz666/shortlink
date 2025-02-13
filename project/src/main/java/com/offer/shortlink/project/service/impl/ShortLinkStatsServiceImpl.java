package com.offer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.offer.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.offer.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.offer.shortlink.project.dao.mapper.*;
import com.offer.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.offer.shortlink.project.dto.resp.*;
import com.offer.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author rwz
 * @since 2025/2/9
 */
@Service
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {

        //基础访问数据 每日的 pv、uv、uip
        List<LinkAccessStatsDO> linkStatsByShortLink = linkAccessStatsMapper.linkStatsByShortLink(requestParam);
        if (CollUtil.isEmpty(linkStatsByShortLink)) {
            return null;
        }

        //基础访问数据 总的pv、uv、uip
        LinkAccessStatsDO pvUvUipStatsByShortLink = linkAccessLogsMapper.findPvUvUipStatsByShortLink(requestParam);

        //访问地区数据(仅国内) top10
        List<ShortLinkStatsLocaleCNTop10RespDTO> listLocaleTop10ByShortLink = linkLocaleStatsMapper.listLocaleTop10ByShortLink(requestParam);
        int localeCntSum = listLocaleTop10ByShortLink.stream().mapToInt(ShortLinkStatsLocaleCNTop10RespDTO::getCnt).sum();
        listLocaleTop10ByShortLink.forEach(item -> {
            double ratio = ((double) item.getCnt() / localeCntSum);
            item.setRatio(Math.round(ratio * 100) / 100.0);
        });

        //24小时访问数据统计
        List<Integer> hourStats = new ArrayList<>();
        List<LinkAccessStatsDO> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
        for(int i = 0 ; i < 24 ; i ++) {
            AtomicInteger hour = new AtomicInteger(i);
            Integer hourCnt = listHourStatsByShortLink.stream()
                    .filter(each -> Objects.equals(each.getHour(), hour.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            hourStats.add(hourCnt);
        }

        //高频IP数据统计（频率最高的前五ip）
        List<HashMap<String, Object>> listTop5IpByShortLinkMap = linkAccessLogsMapper.listTop5IpByShortLink(requestParam);
        List<ShortLinkStatsTopIpRespDTO> listTop5IpByShortLink = BeanUtil.copyToList(listTop5IpByShortLinkMap, ShortLinkStatsTopIpRespDTO.class);

        //一周分布访问详情
        List<LinkAccessStatsDO> listWeekdayStatsByShortLink = linkAccessStatsMapper.listWeekdayStatsByShortLink(requestParam);
        ArrayList<Integer> weekdayStats = new ArrayList<>();
        for(int i = 0 ; i < 7 ; i ++) {
            AtomicInteger weekday = new AtomicInteger(i);
            Integer weekdayCnt = listWeekdayStatsByShortLink.stream()
                    .filter(each -> Objects.equals(each.getWeekday(), weekday.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            weekdayStats.add(weekdayCnt);
        }

        //操作系统访问详情
        List<HashMap<String, Object>> listOsStatsByShortLinkMap = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
        List<ShortLinkStatsOsRespDTO> osStats = BeanUtil.copyToList(listOsStatsByShortLinkMap, ShortLinkStatsOsRespDTO.class);
        int osSumCnt = osStats.stream().mapToInt(ShortLinkStatsOsRespDTO::getCnt).sum();
        osStats.forEach(item -> {
            double ratio = ((double) item.getCnt() / osSumCnt);
            item.setRatio(Math.round(ratio * 100) / 100.0);
        });

        //浏览器访问详情
        List<HashMap<String, Object>> listBrowserStatsByShortLinkMap = linkBrowserStatsMapper.listBrowserStatsByShortLink(requestParam);
        List<ShortLinkStatsBrowserRespDTO> browserStats = BeanUtil.copyToList(listBrowserStatsByShortLinkMap, ShortLinkStatsBrowserRespDTO.class);
        int browserSumCnt = browserStats.stream().mapToInt(ShortLinkStatsBrowserRespDTO::getCnt).sum();
        browserStats.forEach(item -> {
            double ratio = ((double) item.getCnt() / browserSumCnt);
            item.setRatio(Math.round(ratio * 100) / 100.0);
        });

        //访客类型详情
        HashMap<String, Object> findUvTypeCntByShortLink = linkAccessLogsMapper.findUvTypeCntByShortLink(requestParam);
        int oldUserCnt = Integer.parseInt(findUvTypeCntByShortLink.get("oldUserCnt").toString());
        int newUserCnt = Integer.parseInt(findUvTypeCntByShortLink.get("newUserCnt").toString());
        int allUserCnt = oldUserCnt + newUserCnt;
        double oldRatio = (double) oldUserCnt / allUserCnt;
        double newRatio = (double) newUserCnt / allUserCnt;
        ArrayList<ShortLinkStatsUvRespDTO> uvTypeStats = new ArrayList<>();
        ShortLinkStatsUvRespDTO oldUvType = ShortLinkStatsUvRespDTO.builder()
                .cnt(oldUserCnt)
                .uvType("oldUser")
                .ratio(Math.round(oldRatio * 100) / 100.0)
                .build();
        ShortLinkStatsUvRespDTO newUvType = ShortLinkStatsUvRespDTO.builder()
                .cnt(newUserCnt)
                .uvType("newUser")
                .ratio(Math.round(newRatio * 100) / 100.0)
                .build();
        uvTypeStats.add(oldUvType);
        uvTypeStats.add(newUvType);

        //访问网络详情
        List<HashMap<String,Object>> listNetworkStatsByShortLink = linkNetworkStatsMapper.listNetworkStatsByShortLink(requestParam);
        List<ShortLinkStatsNetworkRespDTO> networkStats = BeanUtil.copyToList(listNetworkStatsByShortLink, ShortLinkStatsNetworkRespDTO.class);
        int networkSunCnt = networkStats.stream().mapToInt(ShortLinkStatsNetworkRespDTO::getCnt).sum();
        networkStats.forEach(item -> {
            double ratio = ((double) item.getCnt() / networkSunCnt);
            item.setRatio(Math.round(ratio * 100) / 100.0);
        });

        //访问设备详情
        List<HashMap<String, Object>> listDeviceStatsByShortLink = linkDeviceStatsMapper.listDeviceStatsByShortLink(requestParam);
        List<ShortLinkStatsDeviceRespDTO> deviceStats = BeanUtil.copyToList(listDeviceStatsByShortLink, ShortLinkStatsDeviceRespDTO.class);
        int deviceSumCnt = deviceStats.stream().mapToInt(ShortLinkStatsDeviceRespDTO::getCnt).sum();
        deviceStats.forEach(item -> {
            double ratio = ((double) item.getCnt() / deviceSumCnt);
            item.setRatio(Math.round(ratio * 100) / 100.0);
        });
        return ShortLinkStatsRespDTO.builder()
                .pv(pvUvUipStatsByShortLink.getPv())
                .uv(pvUvUipStatsByShortLink.getUv())
                .uip(pvUvUipStatsByShortLink.getUip())
                .daily(BeanUtil.copyToList(linkStatsByShortLink, ShortLinkStatsDailyRespDTO.class))
                .localeCnStats(listLocaleTop10ByShortLink)
                .hourStats(hourStats)
                .topIpStats(listTop5IpByShortLink)
                .weekdayStats(weekdayStats)
                .osStats(osStats)
                .browserStats(browserStats)
                .uvTypeStats(uvTypeStats)
                .networkStats(networkStats)
                .deviceStats(deviceStats)
                .build();
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        String gid = requestParam.getGid();
        String fullShortUrl = requestParam.getFullShortUrl();
        LambdaQueryWrapper<LinkAccessLogsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getGid, gid)
                .eq(LinkAccessLogsDO::getFullShortUrl, fullShortUrl)
                .between(LinkAccessLogsDO::getCreateTime, requestParam.getStartDate(), requestParam.getEndDate())
                .eq(LinkAccessLogsDO::getDelFlag, 0)
                .orderByDesc(LinkAccessLogsDO::getCreateTime);
        IPage<LinkAccessLogsDO> linkAccessLogsDOIPage = linkAccessLogsMapper.selectPage(requestParam, queryWrapper);
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = linkAccessLogsDOIPage.convert(each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class));

        Set<String> userSet = actualResult.getRecords().stream()
                .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                .collect(Collectors.toSet());
        List<String> userSetList = userSet.stream().toList();
        if (CollectionUtil.isEmpty(userSetList)) {
            return actualResult;
        }
        List<HashMap<String,Object>> userTypeList = linkAccessLogsMapper
                .selectUvTypeByUsers(gid, fullShortUrl, requestParam.getStartDate(), requestParam.getEndDate(), userSetList);
        actualResult.getRecords().forEach(item -> {
            HashMap<String, Object> uvTypeMap = userTypeList.stream()
                    .filter(each -> Objects.equals(each.get("user"), item.getUser()))
                    .findFirst()
                    .orElse(null);
            if (uvTypeMap != null) {
                String uvType = uvTypeMap.get("uvType").toString();
                item.setUvType(uvType);
            }
        });
        return actualResult;
    }
}
