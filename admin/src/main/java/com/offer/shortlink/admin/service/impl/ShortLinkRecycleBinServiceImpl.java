package com.offer.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.offer.shortlink.admin.common.biz.user.UserContext;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.dao.entity.GroupDO;
import com.offer.shortlink.admin.dao.mapper.GroupMapper;
import com.offer.shortlink.admin.remote.ShortLinkRemoteService;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.offer.shortlink.admin.service.ShortLinkRecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rwz
 * @since 2025/2/6
 * 短链接回收站管理接口层实现类
 */
@Service
@RequiredArgsConstructor
public class ShortLinkRecycleBinServiceImpl implements ShortLinkRecycleBinService {

    private final GroupMapper groupMapper;

    /**
     * 后续重构为SpringCloud Feign调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ClientException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        return shortLinkRemoteService.pageRecycleBinShortLink(requestParam);
    }
}
