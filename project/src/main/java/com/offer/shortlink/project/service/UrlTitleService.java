package com.offer.shortlink.project.service;

/**
 * @author rwz
 * @since 2025/2/6
 * URL标题接口层
 */
public interface UrlTitleService {

    /**
     * 根据Url获取网站标题
     *
     * @param url 目标网站地址
     * @return 网站标题
     */
    String getTitleByUrl(String url);
}
