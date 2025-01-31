package com.offer.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rwz
 * @since 2025/2/1
 * 有效期类型枚举
 */
@Getter
@RequiredArgsConstructor
public enum ValidDateTypeEnum {

    PERMANENT(0, "永久有效"),
    CUSTOM(1, "自定义");

    private final int type;
    private final String desc;

}
