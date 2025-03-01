package com.offer.shortlink.admin.config;

import com.offer.shortlink.admin.common.biz.user.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * @author rwz
 * @since 2025/3/1
 * openFeign 微服务调用传递用户信息配置
 */
public class OpenFeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("username", UserContext.getUsername());
            template.header("userId", UserContext.getUserId());
            template.header("realName", UserContext.getRealName());
        };
    }
}
