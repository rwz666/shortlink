package com.offer.shortlink.project.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.offer.shortlink.project.common.convention.exception.ServiceException;
import com.offer.shortlink.project.dao.entity.*;
import com.offer.shortlink.project.dao.mapper.*;
import com.offer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.offer.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.offer.shortlink.project.common.constant.RedisKeyConstant.LOCK_GID_UPDATE_KEY;
import static com.offer.shortlink.project.common.constant.ShortLinkConstant.AMAP_REMOTE_GET_LOCALE_BY_IP_URL;

/**
 * @author rwz
 * @since 2025/2/20
 * 短链接监控状态保存消息队列消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "${rocketmq.producer.topic}",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class ShortLinkStatsSaveConsumer implements StreamListener<String, MapRecord<String, String, String>>, RocketMQListener<Map<String, String>> {

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
    private final ShortLinkMapper shortLinkMapper;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapApiKey;  // 高德地图 API Key

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String stream = message.getStream();
        RecordId id = message.getId();
        if (messageQueueIdempotentHandler.isMessageProcessed(id.toString())) {
            // 判断当前的消息是否已经逻辑执行完成（防止设置了幂等但是没有执行：如服务器宕机）
            if (messageQueueIdempotentHandler.isAccomplished(id.toString())) {
                return;
            }
            throw new ServiceException("[Redis-Stream 消息队列] 消息未成功处理，需要消息队列重试");
        }
        try {
            Map<String, String> producerMap = message.getValue();
            String fullShortUrl = producerMap.get("fullShortUrl");
            if (StrUtil.isNotBlank(fullShortUrl)) {
                String gid = producerMap.get("gid");
                ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("statsRecord"), ShortLinkStatsRecordDTO.class);
                actualSaveShortLinkStats(fullShortUrl, gid, statsRecord);
            }
            // 消息消费完成，将其从redis中删除
            stringRedisTemplate.opsForStream().delete(Objects.requireNonNull(stream), id.getValue());
        } catch (Throwable e) {
            messageQueueIdempotentHandler.delMessageProcessed(id.toString());
            log.error("记录短链接监控消费异常", e);
        }
        //消息完成逻辑处理，设置状态为已完成
        messageQueueIdempotentHandler.setAccomplished(id.toString());
    }


    @Override
    public void onMessage(Map<String, String> producerMap) {
        String keys = producerMap.get("keys");
        if (messageQueueIdempotentHandler.isMessageProcessed(keys)) {
            // 判断当前的消息是否已经逻辑执行完成（防止设置了幂等但是没有执行：如服务器宕机）
            if (messageQueueIdempotentHandler.isAccomplished(keys)) {
                return;
            }
            throw new ServiceException("[RocketMQ 消息队列] 消息未成功处理，需要消息队列重试");
        }
        try {
            String fullShortUrl = producerMap.get("fullShortUrl");
            if (StrUtil.isNotBlank(fullShortUrl)) {
                String gid = producerMap.get("gid");
                ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("statsRecord"), ShortLinkStatsRecordDTO.class);
                actualSaveShortLinkStats(fullShortUrl, gid, statsRecord);
            }
        } catch (Throwable e) {
            log.error("记录短链接监控消费异常", e);
            throw e;
        }
        messageQueueIdempotentHandler.setAccomplished(keys);
    }


    private void actualSaveShortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO statsRecord) {
        fullShortUrl = Optional.ofNullable(fullShortUrl).orElse(statsRecord.getFullShortUrl());
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        try {
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
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .pv(1)
                    .uv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .uip(statsRecord.getUipFirstFlag() ? 1 : 0)
                    .date(nowDate)
                    .weekday(weekday.getIso8601Value())
                    .hour(hour)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);
            //2.地区访问数据统计
            //调用高德地图api，根据ip获取地理位置
            HashMap<String, Object> getLocaleParam = new HashMap<>();
            getLocaleParam.put("key", statsLocaleAmapApiKey);
            getLocaleParam.put("ip", statsRecord.getRemoteAddr());
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_GET_LOCALE_BY_IP_URL, getLocaleParam);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            String actualProvince = "", actualCity = "";
            if (Objects.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = Objects.equals(province, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
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
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(nowDate)
                    .os(statsRecord.getOs())
                    .cnt(1)
                    .build();
            linkOsStatsMapper.shortLinkOsStats(linkOsStatsDO);
            //4.浏览器访问数据统计
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(nowDate)
                    .cnt(1)
                    .browser(statsRecord.getBrowser())
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserStats(linkBrowserStatsDO);

            //5.访问设备数据统计
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .cnt(1)
                    .date(nowDate)
                    .device(statsRecord.getDevice())
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceStats(linkDeviceStatsDO);
            //6.访问网络数据统计

            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .cnt(1)
                    .date(nowDate)
                    .network(statsRecord.getNetwork())
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkStats(linkNetworkStatsDO);

            //7.访问日志数据统计
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(nowDate)
                    .os(statsRecord.getOs())
                    .ip(statsRecord.getRemoteAddr())
                    .browser(statsRecord.getBrowser())
                    .user(statsRecord.getUv())
                    .device(statsRecord.getDevice())
                    .network(statsRecord.getNetwork())
                    .locale(StrUtil.join("-", "中国", actualProvince, actualCity))
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);

            shortLinkMapper.incrementStats(gid, fullShortUrl, 1, statsRecord.getUvFirstFlag() ? 1 : 0, statsRecord.getUipFirstFlag() ? 1 : 0);

            //今日访问监控
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .todayPv(1)
                    .todayUv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .todayUip(statsRecord.getUipFirstFlag() ? 1 : 0)
                    .date(nowDate)
                    .build();
            linkStatsTodayMapper.shortLinkTodayStats(linkStatsTodayDO);
        } catch (Throwable e) {
            log.error("短链接：{}访问量统计异常", fullShortUrl, e);
        } finally {
            rLock.unlock();
        }
    }
}
