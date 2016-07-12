package com.knowall.findots.restcalls.forgotPassword;

import org.parceler.Parcel;

/**
 * Created by parijathar on 6/17/2016.
 */
@Parcel
public class ForgotPasswordModel {
    private String message;

    private String errorCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", errorCode = " + errorCode + "]";
    }
}
