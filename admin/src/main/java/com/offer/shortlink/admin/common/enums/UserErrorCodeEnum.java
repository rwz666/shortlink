package com.offer.shortlink.admin.common.enums;

import com.offer.shortlink.admin.common.convention.errorcode.IErrorCode;
import lombok.Getter;

/**
 * @author rwz
 * @since 2025/1/22
 */
@Getter
public enum UserErrorCodeEnum implements IErrorCode {

    USER_NULL("B00200", "用户记录不存在"),
    USER_EXIST("B002001", "用户记录已存在");

    private final String code;
    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
