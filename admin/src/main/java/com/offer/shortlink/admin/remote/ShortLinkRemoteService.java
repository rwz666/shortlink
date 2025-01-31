package com.offer.shortlink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.offer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.List;

/**
 * @author rwz
 * @since 2025/1/30
 * 短链接中台远程服务调用
 */
public interface ShortLinkRemoteService {

    //中台地址
    String SHORT_LINK_PROJECT = "http://127.0.0.1:8001";


    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建响应
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post(SHORT_LINK_PROJECT + "/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 分页查询短链接
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultJsonStr = HttpUtil.get(SHORT_LINK_PROJECT + "/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultJsonStr, new TypeReference<>() {
        });
    }

    /**
     * 查询分组短链接总量
     *
     * @param requestParam 查询分组短链接总量请求参数
     * @return 查询分组短链接总量响应
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listShortLinkGroupCount(List<String> requestParam) {
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestParam", requestParam);
        String resultJsonStr = HttpUtil.get(SHORT_LINK_PROJECT + "/api/short-link/v1/count", requestMap);
        return JSON.parseObject(resultJsonStr, new TypeReference<>() {
        });
    }

    /**
     * 修改短链接
     *
     * @param requestParam 修改短链接请求参数
     */
    default void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        HttpUtil.post(SHORT_LINK_PROJECT + "/api/short-link/v1/update", JSON.toJSONString(requestParam));
    }
}
