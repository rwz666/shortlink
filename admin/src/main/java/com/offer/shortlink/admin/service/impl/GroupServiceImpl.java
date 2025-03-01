package com.offer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.admin.common.biz.user.UserContext;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.convention.exception.ServiceException;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dao.entity.GroupUniqueDO;
import com.offer.shortlink.admin.dao.mapper.GroupMapper;
import com.offer.shortlink.admin.dao.mapper.GroupUniqueMapper;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.offer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.offer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.offer.shortlink.admin.remote.ShortLinkRemoteClient;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.offer.shortlink.admin.service.GroupService;
import com.offer.shortlink.admin.toolkit.RandomStringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.offer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_GROUP_CREATE_KEY;

/**
 * @author rwz
 * @since 2025/1/24
 * 短链接分组接口层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final ShortLinkRemoteClient shortLinkRemoteClient;

    private final RedissonClient redissonClient;
    private final RBloomFilter<String> gidRegisterCachePenetrationBloomFilter;
    private final GroupUniqueMapper groupUniqueMapper;

    @Value("${short-link.group.max-num}")
    private Integer groupMaxNum;

    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }

    @Override
    public void saveGroup(String username, String groupName) {
        RLock lock = redissonClient.getLock(LOCK_GROUP_CREATE_KEY);
        lock.lock();
        try {
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0);
            Long groupDOCount = baseMapper.selectCount(queryWrapper);
            if (groupDOCount.intValue() == groupMaxNum) {
                throw new ClientException(String.format("已超出最大分组数：%d", groupMaxNum));
            }
            String gid = null;
            int retryCount = 0;
            int maxRetries = 10;
            while (retryCount < maxRetries) {
                gid = saveGroupUniqueReturnGid();
                if (StrUtil.isNotBlank(gid)) {
                    break;
                }
                retryCount++;
            }
            if (StrUtil.isBlank(gid)) {
                throw new ServiceException("生成分组标识频繁");
            }
            GroupDO groupDO = GroupDO.builder()
                    .gid(gid)
                    .username(username)
                    .name(groupName)
                    .sortOrder(0)
                    .build();
            int inserted = baseMapper.insert(groupDO);
            if (inserted < 1) {
                throw new ServiceException("数据库插入失败");
            }
            gidRegisterCachePenetrationBloomFilter.add(gid);
        }finally {
            lock.unlock();
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
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteClient
                .listShortLinkGroupCount(groupDOList.stream().map(GroupDO::getGid).toList());
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        shortLinkGroupRespDTOList.forEach(each -> {
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), each.getGid()))
                    .findFirst();
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupRespDTOList;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, queryWrapper);

    }

    @Override
    public void deleteGroup(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, queryWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(item -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(item.getSortOrder())
                    .build();
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, item.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, queryWrapper);
        });
    }


    private String saveGroupUniqueReturnGid() {
        String gid = RandomStringUtil.generateRandom();
        if (!gidRegisterCachePenetrationBloomFilter.contains(gid)) {
            GroupUniqueDO groupUniqueDO = GroupUniqueDO.builder()
                    .gid(gid)
                    .build();
            try {
                groupUniqueMapper.insert(groupUniqueDO);
            }catch (DuplicateKeyException e) {
                return null;
            }
        }
        return gid;
    }
}
