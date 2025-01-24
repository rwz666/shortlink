package com.offer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dao.mapper.GroupMapper;
import com.offer.shortlink.admin.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author rwz
 * @since 2025/1/24
 * 短链接分组接口层实现类
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
}
