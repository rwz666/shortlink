package com.offer.shortlink.admin.common.convention.exception;

import com.offer.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.offer.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * @author rwz
 * @since 2025/1/23
 */
public class RemoteException extends AbstractException{

    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
