package com.offer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.project.common.convention.exception.ClientException;
import com.offer.shortlink.project.common.convention.exception.ServiceException;
import com.offer.shortlink.project.common.enums.ValidDateTypeEnum;
import com.offer.shortlink.project.dao.entity.*;
import com.offer.shortlink.project.dao.mapper.*;
import com.offer.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.project.dto.resp.*;
import com.offer.shortlink.project.service.ShortLinkService;
import com.offer.shortlink.project.toolkit.HashUtil;
import com.offer.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.offer.shortlink.project.common.constant.RedisKeyConstant.*;
import static com.offer.shortlink.project.common.constant.ShortLinkConstant.AMAP_REMOTE_GET_LOCALE_BY_IP_URL;

/**
 * @author rwz
 * @since 2025/1/26
 * 短链接接口层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;

    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapApiKey;  // 高德地图 API Key

    @Value("${short-link.domain.default}")
    private String shortLinkDefaultDomain;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateShortLinkSuffix(requestParam);
        String fullShortUrl = shortLinkDefaultDomain + "/" + shortLinkSuffix;
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(shortLinkDefaultDomain)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createType(requestParam.getCreateType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .favicon(getFaviconUrl(requestParam.getOriginUrl()))
                .shortUri(shortLinkSuffix)
                .fullShortUrl(fullShortUrl)
                .enableStatus(0)
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException ex) {
            //数据库插入失败，索引冲突
            if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
            }
            throw new ServiceException(String.format("短链接：%s 生成重复", fullShortUrl));
        }
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()), TimeUnit.MILLISECONDS
        );
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO
                .builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .build();
    }

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        ArrayList<ShortLinkBaseInfoRespDTO> resultList = new ArrayList<>();
        for (int i = 0; i < originUrls.size(); i++) {
            ShortLinkCreateReqDTO shortLinkCreateReqDTO = BeanUtil.toBean(requestParam, ShortLinkCreateReqDTO.class);
            shortLinkCreateReqDTO.setOriginUrl(originUrls.get(i));
            shortLinkCreateReqDTO.setDescribe(describes.get(i));
            try {
                ShortLinkCreateRespDTO shortLinkCreateRespDTO = createShortLink(shortLinkCreateReqDTO);
                ShortLinkBaseInfoRespDTO resultBean = BeanUtil.toBean(shortLinkCreateRespDTO, ShortLinkBaseInfoRespDTO.class);
                resultList.add(resultBean);
            }catch (Throwable e) {
                log.error("批量创建短链接失败，原始链接：{}", originUrls.get(i));
            }
        }
        return ShortLinkBatchCreateRespDTO.builder()
                .total(resultList.size())
                .baseLinkInfos(resultList)
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkDO> resultPage = baseMapper.pageLink(requestParam);
        return resultPage.convert(item -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(item, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listShortLinkGroupCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO hasShortLink = baseMapper.selectOne(queryWrapper);
        if (hasShortLink == null) {
            throw new ClientException("短链接不存在");
        }

        ShortLinkDO shortLinkDO = ShortLinkDO
                .builder()
                .domain(hasShortLink.getDomain())
                .shortUri(hasShortLink.getShortUri())
                .clickNum(hasShortLink.getClickNum())
                .favicon(hasShortLink.getFavicon())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        if (Objects.equals(requestParam.getGid(), hasShortLink.getGid())) {
            //没有修改gid,直接修改
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), ValidDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            //修改了gid，先删除原来的，再插入
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, hasShortLink.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);
        }


    }

    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        String serverName = request.getServerName();
        String serverPort = Optional.of(request.getServerPort())
                .filter(port -> !Objects.equals(port, 80))
                .map(String::valueOf)
                .map(port -> ":" + port)
                .orElse("");
        String fullShortUrl = serverName + serverPort + "/" + shortUri;
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originalLink)) {
            shortLinkStats(null, fullShortUrl, request, response);
            response.sendRedirect(originalLink);
            return;
        }
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            response.sendRedirect("/page/notfound");
            return;
        }
        String gotoShortLinkIsNull = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoShortLinkIsNull)) {
            response.sendRedirect("/page/notfound");
            return;
        }
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalLink)) {
                shortLinkStats(null, fullShortUrl, request, response);
                response.sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoWrapper);
            if (shortLinkGotoDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid());
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO == null || (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date()))) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
                return;
            }
            String originUrl = shortLinkDO.getOriginUrl();
            stringRedisTemplate.opsForValue().set(
                    String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                    originUrl,
                    LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS
            );
            shortLinkStats(shortLinkDO.getGid(), fullShortUrl, request, response);
            response.sendRedirect(originUrl);
        } finally {
            lock.unlock();
        }

    }

    private void shortLinkStats(String gid, String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        AtomicBoolean uvFirstFlag = new AtomicBoolean(false);
        try {
            AtomicReference<String> uv = new AtomicReference<>();
            Runnable addResponseCookieTask = () -> {
                //用户第一次请求，添加cookie标识（有效期一个月之后算新用户）
                uv.set(UUID.fastUUID().toString());
                Cookie uvCookie = new Cookie("uv", uv.get());
                uvCookie.setMaxAge(60 * 60 * 24 * 30);//有效期一个月
                uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
                stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uv.get());
                uvFirstFlag.set(Boolean.TRUE);
                response.addCookie(uvCookie);
            };
            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(item -> Objects.equals(item.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(item -> {
                            //如果存在uv的cookie，说明不是该用户第一次请求短链接
                            uv.set(item);
                            Long uvAdd = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, item);
                            uvFirstFlag.set(uvAdd != null && uvAdd > 0L);
                        }, addResponseCookieTask);
            } else {
                addResponseCookieTask.run();
            }
            //gid为null，重新查询数据库获取
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }
            Date nowDate = new Date();
            //1.基础访问数据统计
            Week weekday = DateUtil.dayOfWeekEnum(nowDate);
            int hour = DateUtil.hour(nowDate, true);
            String actualIp = LinkUtil.getActualIp(request);
            Long uipAdd = stringRedisTemplate.opsForSet().add("short-link:stats:uip:" + fullShortUrl, actualIp);
            boolean uipFirstFlag = uipAdd != null && uipAdd > 0L;
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .pv(1)
                    .uv(uvFirstFlag.get() ? 1 : 0)
                    .uip(uipFirstFlag ? 1 : 0)
                    .date(nowDate)
                    .weekday(weekday.getIso8601Value())
                    .hour(hour)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            //2.地区访问数据统计
            //调用高德地图api，根据ip获取地理位置
            HashMap<String, Object> getLocaleParam = new HashMap<>();
            getLocaleParam.put("key", statsLocaleAmapApiKey);
            getLocaleParam.put("ip", actualIp);
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_GET_LOCALE_BY_IP_URL, getLocaleParam);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            String actualProvince = "", actualCity = "";
            if (Objects.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = Objects.equals(province, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .gid(gid)
                        .fullShortUrl(fullShortUrl)
                        .date(nowDate)
                        .cnt(1)
                        .country("中国")
                        .province(actualProvince = unknownFlag ? "未知" : province)
                        .city(actualCity = unknownFlag ? "未知" : localeResultObj.getString("city"))
                        .adcode(unknownFlag ? "未知" : localeResultObj.getString("adcode"))
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleStats(linkLocaleStatsDO);
            }
            //3.操作系统访问数据统计
            String os = LinkUtil.getOs(request);
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(nowDate)
                    .os(os)
                    .cnt(1)
                    .build();
            linkOsStatsMapper.shortLinkOsStats(linkOsStatsDO);
            //4.浏览器访问数据统计
            String browser = LinkUtil.getBrowserName(request);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(nowDate)
                    .cnt(1)
                    .browser(browser)
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserStats(linkBrowserStatsDO);

            //5.访问设备数据统计
            String device = LinkUtil.getDevice(request);
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .cnt(1)
                    .date(nowDate)
                    .device(device)
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceStats(linkDeviceStatsDO);
            //6.访问网络数据统计
            String network = LinkUtil.getNetwork(request);
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .cnt(1)
                    .date(nowDate)
                    .network(network)
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkStats(linkNetworkStatsDO);

            //7.访问日志数据统计
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(nowDate)
                    .os(os)
                    .ip(actualIp)
                    .browser(browser)
                    .user(uv.get())
                    .device(device)
                    .network(network)
                    .locale(StrUtil.join("-", "中国", actualProvince, actualCity))
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);

            baseMapper.incrementStats(gid, fullShortUrl, 1, uvFirstFlag.get() ? 1 : 0, uipFirstFlag ? 1 : 0);

            //今日访问监控
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .todayPv(1)
                    .todayUv(uvFirstFlag.get() ? 1 : 0)
                    .todayUip(uipFirstFlag ? 1 : 0)
                    .date(nowDate)
                    .build();
            linkStatsTodayMapper.shortLinkTodayStats(linkStatsTodayDO);
        } catch (Throwable e) {
            log.error("短链接：{}访问量统计异常", fullShortUrl, e);
        }
    }

    private String generateShortLinkSuffix(ShortLinkCreateReqDTO requestParam) {
        int curGenerateCount = 0;
        String shortUri;
        while (true) {
            if (curGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成，请稍后重试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString();
            shortUri = HashUtil.hashToBase62(originUrl);
            if (!shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)) {
                break;
            }
            curGenerateCount++;
        }
        return shortUri;
    }

    @SneakyThrows
    private String getFaviconUrl(String url) {
        // 发送 HTTP 请求并获取 HTML 文档
        Document document = Jsoup.connect(url).get();
        // 查找 favicon 的 <link> 标签
        Element faviconElement = document.select("link[rel~=(?i)^(shortcut|icon|shortcut icon)$]").first();
        if (faviconElement != null) {
            // 获取 favicon 的 URL
            String faviconUrl = faviconElement.attr("href");
            // 如果 URL 是相对路径，补全为绝对路径
            if (!faviconUrl.startsWith("http")) {
                faviconUrl = url + (faviconUrl.startsWith("/") ? "" : "/") + faviconUrl;
            }
            return faviconUrl;
        }
        return null;
    }
}
