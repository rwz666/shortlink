package com.offer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author rwz
 * @since 2025/1/24
 */
@Data
public class UserLoginReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
