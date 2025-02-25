package com.offer.shortlink.gateway.config;

import lombok.Data;

import java.util.List;

/**
 * @author rwz
 * @since 2025/2/25
 * 过滤器配置
 */
@Data
public class Config {

    /**
     * 白名单前置路径
     */
    private List<String> whitePathList;
}
