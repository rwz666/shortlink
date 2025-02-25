package com.offer.shortlink.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.offer.shortlink.gateway.config.Config;
import com.offer.shortlink.gateway.dto.GatewayErrorResult;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author rwz
 * @since 2025/2/25
 * SpringCloud Gateway Token 拦截器
 */
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    private final StringRedisTemplate stringRedisTemplate;

    public TokenValidateGatewayFilterFactory(StringRedisTemplate stringRedisTemplate) {
        super(Config.class);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String requestPath = request.getPath().toString();
            String requestMethod = request.getMethod().name();
            // 白名单内，直接放行
            if (isPathInWhiteList(requestPath, requestMethod, config.getWhitePathList())) {
                return chain.filter(exchange);
            }
            HttpHeaders headers = request.getHeaders();
            String username = headers.getFirst("username");
            String token = headers.getFirst("token");
            Object userInfo = null;
            if (StringUtils.hasText(token)) {
                userInfo = stringRedisTemplate.opsForHash().get("short-link:login:" + username, token);
            }
            // 验证通过
            if (StringUtils.hasText(username) && StringUtils.hasText(token) && null != userInfo) {
                JSONObject userInfoJsonObject = JSON.parseObject(userInfo.toString());
                ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set("userId", userInfoJsonObject.getString("id"));
                    httpHeaders.set("realName", URLEncoder.encode(userInfoJsonObject.getString("realName"), StandardCharsets.UTF_8));
                });
                return chain.filter(exchange.mutate().request(builder.build()).build());
            }
            return buildErrorResponse(response, "Token Validation Error", HttpStatus.UNAUTHORIZED);
        };
    }

    // 判断 请求是否在白名单内
    private boolean isPathInWhiteList(String requestPath, String requestMethod, List<String> whiteList) {
        if (Objects.equals(requestPath, "/api/short-link/admin/v1/user") && Objects.equals(requestMethod, "POST")) {
            return true;
        }
        return !CollectionUtils.isEmpty(whiteList) && whiteList.stream().anyMatch(requestPath::startsWith);
    }

    // 构建自定义错误响应
    private Mono<Void> buildErrorResponse(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
        // 创建自定义错误对象
        GatewayErrorResult errorResponse = GatewayErrorResult.builder()
                .status(status.value())
                .message(message)
                .build();
        // 返回错误响应
        return response.writeWith(Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(errorResponse))));
    }

}
