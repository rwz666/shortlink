package com.offer.shortlink.admin.controller;

import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.remote.ShortLinkRemoteClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rwz
 * @since 2025/2/6
 * URL标题控制层
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    private final ShortLinkRemoteClient shortLinkRemoteClient;

    /**
     * 根据Url获取网页标题
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return shortLinkRemoteClient.getTitleByUrl(url);
    }
}
