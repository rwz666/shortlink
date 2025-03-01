package com.offer.shortlink.admin.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.config.OpenFeignConfiguration;
import com.offer.shortlink.admin.remote.dto.req.*;
import com.offer.shortlink.admin.remote.dto.resp.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author rwz
 * @since 2025/2/23
 * 短链接远程Feign调用客户端
 */
@FeignClient(
        value = "short-link-project",
        configuration = OpenFeignConfiguration.class
)
public interface ShortLinkRemoteClient {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 短链接创建响应
     */
    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 批量短链接创建返回对象
     */
    @PostMapping("/api/short-link/v1/create/batch")
    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortLinkPageRespDTO>> pageShortLink(@SpringQueryMap ShortLinkPageReqDTO requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam 修改短链接请求参数
     */
    @PostMapping("/api/short-link/v1/update")
    void updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);

    /**
     * 查询分组短链接总量
     *
     * @param requestParam 查询分组短链接总量请求参数
     * @return 查询分组短链接总量响应
     */
    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkGroupCountQueryRespDTO>> listShortLinkGroupCount(@RequestParam("requestParam") List<String> requestParam);

    /**
     * 根据Url获取网站标题
     *
     * @param url 目标网站地址
     * @return 网站标题
     */
    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam("url") String url);

    /**
     * 新增回收站数据
     *
     * @param requestParam 新增回收站数据请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    void saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 分页查询回收站短链接请求参数
     * @return 分页查询回收站数据返回
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(@SpringQueryMap ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 恢复回收站短链接
     *
     * @param requestParam 恢复回收站短链接请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 移除短链接
     *
     * @param requestParam 移除短链接请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);

    /**
     * 短链接监控
     *
     * @param requestParam 短链接监控请求对象
     * @return 短链接监控返回
     */
    @GetMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> oneShortLinkStats(@SpringQueryMap ShortLinkStatsReqDTO requestParam);

    /**
     * 获取单个短链接访问记录数据
     *
     * @param requestParam 获取单个短链接访问记录数据请求对象
     * @return 分页结果
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(@SpringQueryMap ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 分组短链接监控
     *
     * @param requestParam 分组短链接监控请求对象
     * @return 分组短链接监控返回
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> groupShortLinkStats(@SpringQueryMap ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 获取分组短链接访问记录数据
     *
     * @param requestParam 获取分组短链接访问记录数据请求对象
     * @return 分页结果
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(@SpringQueryMap ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
