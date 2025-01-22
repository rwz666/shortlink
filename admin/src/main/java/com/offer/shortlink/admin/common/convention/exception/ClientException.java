package com.offer.shortlink.admin.common.convention.exception;

import com.offer.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.offer.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * @author rwz
 * @since 2025/1/22
 */
public class ClientException extends AbstractException {

    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }

}
