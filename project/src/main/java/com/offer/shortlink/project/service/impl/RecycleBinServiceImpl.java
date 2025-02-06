package com.offer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.offer.shortlink.project.common.convention.exception.ServiceException;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import com.offer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.offer.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.offer.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.offer.shortlink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;

/**
 * @author rwz
 * @since 2025/2/6
 * 回收站管理接口实现曾
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        String gid = requestParam.getGid();
        String fullShortUrl = requestParam.getFullShortUrl();
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLinkDOUpdate = ShortLinkDO.builder()
                .enableStatus(1)
                .build();
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        int update = baseMapper.update(shortLinkDOUpdate, updateWrapper);
        if (update < 1) {
            throw new ServiceException("短链接放入回收站失败");
        }
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, requestParam.getGidList())
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getUpdateTime);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(item -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(item, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }
}
