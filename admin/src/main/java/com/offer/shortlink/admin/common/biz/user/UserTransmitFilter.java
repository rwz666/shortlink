package com.offer.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.common.enums.UserErrorCodeEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

/**
 * @author rwz
 * @since 2025/1/24
 * 用户信息传输过滤器
 */
@Slf4j
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    public static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username"
    );

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        //忽略的接口
        if (IGNORE_URI.contains(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String method = httpServletRequest.getMethod();
        if (Objects.equals(requestURI, "/api/short-link/admin/v1/user") && Objects.equals(method, "POST")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");
        if (StrUtil.isAllNotBlank(username, token)) {
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
        }
        Object userInfoJsonStr = null;
        try {
            userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);
            if (userInfoJsonStr == null) {
                returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
            }
        } catch (Exception ex) {
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(UserErrorCodeEnum.USER_TOKEN_FAIL))));
        }
        UserInfoDTO userInfoDTO = JSON.parseObject((String) userInfoJsonStr, UserInfoDTO.class);
        UserContext.setUser(userInfoDTO);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception{
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            log.error("response error",e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
