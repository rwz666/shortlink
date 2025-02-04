package com.offer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.offer.shortlink.project.dao.entity.ShortLinkDO;
import com.offer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.offer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.offer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author rwz
 * @since 2025/1/26
 * 短链接接口层
 */
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 短链接分页查询
     *
     * @param requestParam 短链接分页查询请求参数
     * @return 短链接分页查询结果集合
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询分组短链接数量
     *
     * @param requestParam 查询分组短链接数量请求参数
     * @return 查询分组短链接数量响应
     */
    List<ShortLinkGroupCountQueryRespDTO> listShortLinkGroupCount(List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam 修改短链接请求对象
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转
     *
     * @param shortUri 短链接后缀
     * @param request  HTTP 请求
     * @param response HTTP 响应
     */
    void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response);
}
