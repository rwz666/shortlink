package com.offer.shortlink.project.common.biz.user;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rwz
 * @since 2025/1/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDTO {

    /**
     * id
     */
    @JSONField(name = "id")
    private String userId;

    /**
     * 用户名
     */
    private String username;


    /**
     * 真实姓名
     */
    private String realName;

}
