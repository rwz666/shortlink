package com.offer.shortlink.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.remote.dto.req.*;
import com.offer.shortlink.admin.remote.dto.resp.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据Url获取网站标题
     *
     * @param url 目标网站地址
     * @return 网站标题
     */
    default Result<String> getTitleByUrl(@RequestParam("url") String url) {
        String resultStr = HttpUtil.get(SHORT_LINK_PROJECT + "/api/short-link/v1/title?url=" + url);
        return JSON.parseObject(resultStr, new TypeReference<>() {
        });
    }

    /**
     * 新增回收站数据
     *
     * @param requestParam 新增回收站数据请求参数
     */
    default void saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        HttpUtil.post(SHORT_LINK_PROJECT + "/api/short-link/v1/recycle-bin/save", JSON.toJSONString(requestParam));
    }

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 分页查询回收站短链接请求参数
     * @return 分页查询回收站数据返回
     */
    default Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", requestParam.getGidList());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultJsonStr = HttpUtil.get(SHORT_LINK_PROJECT + "/api/short-link/v1/recycle-bin/page", requestMap);
        return JSON.parseObject(resultJsonStr, new TypeReference<>() {
        });
    }

    /**
     * 回复回收站短链接
     *
     * @param requestParam 恢复回收站短链接请求参数
     */
    default void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        HttpUtil.post(SHORT_LINK_PROJECT + "/api/short-link/v1/recycle-bin/recover", JSON.toJSONString(requestParam));
    }

    /**
     * 移除短链接
     *
     * @param requestParam 移除短链接请求参数
     */
    default void removeRecycleBin(RecycleBinRemoveReqDTO requestParam) {
        HttpUtil.post(SHORT_LINK_PROJECT + "/api/short-link/v1/recycle-bin/remove", JSON.toJSONString(requestParam));
    }

    /**
     * 短链接监控
     *
     * @param requestParam 短链接监控请求对象
     * @return 短链接监控返回
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get(SHORT_LINK_PROJECT + "/api/short-link/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    /**
     * 获取单个短链接访问记录数据
     *
     * @param requestParam 获取单个短链接访问记录数据请求对象
     * @return 分页结果
     */
    default Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        Map<String, Object> paramMap = BeanUtil.beanToMap(requestParam, false, true);
        paramMap.remove("orders");
        paramMap.remove("records");
        String resultBodyStr = HttpUtil.get(SHORT_LINK_PROJECT + "/api/short-link/v1/access-record", paramMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }
}
