package com.offer.shortlink.project.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.offer.shortlink.project.common.convention.result.Result;
import com.offer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * @author rwz
 * @since 2025/2/19
 * 自定义流控策略
 */
public class CustomBlockHandler {

    public static Result<ShortLinkCreateRespDTO> createShortLinkBlockHandlerMethod(ShortLinkCreateReqDTO requestParam, BlockException exception) {
        return new Result<ShortLinkCreateRespDTO>().setCode("B100000").setMessage("当前访问网站人数过多，请稍后再试...");
    }
}
