package com.offer.shortlink.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rwz
 * @since 2025/2/25
 * 网关错误返回信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayErrorResult {

    /**
     * HTTP 状态码
     */
    private Integer status;

    /**
     * 返回信息
     */
    private String message;
}
