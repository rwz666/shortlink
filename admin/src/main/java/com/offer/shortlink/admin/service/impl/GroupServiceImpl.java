package com.offer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.common.biz.user.UserContext;
import com.offer.shortlink.admin.common.convention.exception.ServiceException;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dao.mapper.GroupMapper;
import com.offer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.offer.shortlink.admin.service.GroupService;
import com.offer.shortlink.admin.toolkit.RandomStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rwz
 * @since 2025/1/24
 * 短链接分组接口层实现类
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;
        do {
            gid = RandomStringUtil.generateRandom();
        } while (hasGId(gid));
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .username(UserContext.getUsername())
                .name(groupName)
                .sortOrder(0)
                .build();
        int inserted = baseMapper.insert(groupDO);
        if (inserted < 1) {
            throw new ServiceException("数据库插入失败");
        }
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {

        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }

    /**
     * 查询数据库中是否有GId
     *
     * @param gid 分组标识
     * @return 数据库中有返回GId True，没有返回 False
     */
    private Boolean hasGId(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        return groupDO != null;
    }
}
