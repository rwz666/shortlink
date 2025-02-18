package com.offer.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.project.dao.entity.LinkStatsTodayDO;
import com.offer.shortlink.project.dao.mapper.LinkStatsTodayMapper;
import com.offer.shortlink.project.service.LinkStatsTodayService;
import org.springframework.stereotype.Service;

/**
 * @author rwz
 * @since 2025/2/17
 * 短链接今日访问接口层实现类
 */
@Service
public class LinkStatsTodayServiceImpl extends ServiceImpl<LinkStatsTodayMapper, LinkStatsTodayDO> implements LinkStatsTodayService {
}
