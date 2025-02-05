package com.offer.shortlink.project.service.impl;

import com.offer.shortlink.project.service.UrlTitleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author rwz
 * @since 2025/2/6
 * URL标题接口实现层
 */
@Service
public class UrlTitleServiceImpl implements UrlTitleService {

    @Override
    public String getTitleByUrl(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.title();
        } catch (IOException e) {
            return "无法获取网站标题";
        }
    }
}
